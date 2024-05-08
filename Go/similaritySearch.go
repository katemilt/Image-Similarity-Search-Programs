package main

import (
	"fmt"
	"image/jpeg"
	"math"
	"os"
	"path/filepath"
	"sort"
	"sync"
)

type Histo struct {
	Name string
	H    []float64
}

type SimilarityList struct {
	Name       string
	Similarity float64
}

func computeHistograms(imagePath []string, depth int, hChan chan<- Histo) {
	for _, path := range imagePath {
		histo, err := computeHistogram(path, depth)
		if err != nil {
			fmt.Println(err)
			return
		}
		hChan <- histo
	}
}

func computeHistogram(imagePath string, depth int) (Histo, error) {
	file, err := os.Open(imagePath)
	if err != nil {
		return Histo{}, err
	}
	defer file.Close()

	img, err := jpeg.Decode(file)
	if err != nil {
		return Histo{}, err
	}

	width := img.Bounds().Max.X
	height := img.Bounds().Max.Y
	numBins := 1 << (3 * depth)
	histo := make([]float64, numBins)

	for j := 0; j < height; j++ {
		for i := 0; i < width; i++ {
			r, g, b, _ := img.At(i, j).RGBA()
			r = (r >> (16 - depth))
			g = (g >> (16 - depth))
			b = (b >> (16 - depth))

			// Use given formula to calculate index
			histoIndex := (int(r) << (2 * depth)) + (int(g) << depth) + int(b)
			histo[histoIndex]++
		}
	}

	// Normalize histogram
	numPixels := float64(width * height)
	for i := range histo {
		histo[i] /= numPixels
	}

	return Histo{Name: imagePath, H: histo}, nil
}

func compareHistograms(h1, h2 Histo) float64 {
	intersection := 0.0
	for i := 0; i < len(h1.H); i++ {
		intersection += math.Min(h1.H[i], h2.H[i])
	}
	return intersection
}

func getTop5(similarity map[string]float64) {
	var values []SimilarityList

	for filename, similarity := range similarity {
		values = append(values, SimilarityList{Name: filename, Similarity: similarity})
	}

	// Sort by similarity in descending order
	sort.Slice(values, func(i, j int) bool {
		return values[i].Similarity > values[j].Similarity
	})

	// Print top 5 most similar images
	for i, value := range values[:5] {
		fmt.Printf("%d. Filename: %s, Similarity: %f\n", i+1, value.Name, value.Similarity)
	}
}

func main() {
	// K value for slicing, change as needed (1, 2, 4, 16, 64, 256, 1048)
	k := 1
	// Depth parameter
	d := 3

	// Start time for experiments [Uncomment to use]
	// start := time.Now()

	var wg sync.WaitGroup

	// os.Args can be used to access command line args
	if len(os.Args) != 3 {
		fmt.Println("Example usage: go run similaritySearch.go q00.jpg imageDataset2_15_20")
		return
	}

	queryImageFilename := os.Args[1]
	imageDatasetDirectory := os.Args[2]

	// Create the channel of histogram files
	hChan := make(chan Histo)
	defer close(hChan)

	// Get list of all image filename in dataset
	imageDatset, err := os.ReadDir(imageDatasetDirectory)
	if err != nil {
		fmt.Println(err)
		return
	}
	// Get only the files ending in .jpg
	datasetFiles := make([]string, 0, len(imageDatset))
	for _, file := range imageDatset {
		if !file.IsDir() && filepath.Ext(file.Name()) == ".jpg" {
			datasetFiles = append(datasetFiles, filepath.Join(imageDatasetDirectory, file.Name()))
		}
	}

	slices := make([][]string, k) // Slice to hold image slices

	// Split into k slices
	for i := range datasetFiles {
		sliceIndex := i % k
		slices[sliceIndex] = append(slices[sliceIndex], datasetFiles[i])
	}

	// Send each to computeHistograms
	for _, slice := range slices {
		wg.Add(1)
		go func(files []string) {
			defer wg.Done()
			computeHistograms(files, d, hChan)
		}(slice)
	}

	// In separate thread, open query image and send to computeHistogram
	queryHistogram, err := computeHistogram("queryImages/"+queryImageFilename, d)
	if err != nil {
		fmt.Println(err)
		return
	}

	// Read the chan of histograms
	similarity := make(map[string]float64)
	go func() {
		for histogram := range hChan {
			// When histogram received, compare with query
			similarity[histogram.Name] = compareHistograms(queryHistogram, histogram)
		}
	}()

	wg.Wait()

	// Maintain list of 5 most similar
	fmt.Printf("Top 5 images similar to the image: %s\n", queryImageFilename)
	getTop5(similarity)

	// Print execution time [Uncomment to use]
	// fmt.Printf("Execution Time for k=%d: %v\n", k, time.Since(start))
}
