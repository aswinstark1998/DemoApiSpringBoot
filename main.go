package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"sort"
	"strings"

	"github.com/gorilla/mux"
)

//Book Struct (Kind of like class) = Model
// ID is property
type Book struct {
	ID     uint    `json:"id"`
	Isbn   string  `json:"isbn"`
	Title  string  `json:"title"`
	Author *Author `json:"author"`
}

//Author Struct
type Author struct {
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
}

// Init books var as a slice (Variable length array in GoLang) Book struct
var books []Book

// Get All Books
func getBooks(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(books)
}

func encrypt(inp string) string {
	out := []rune(inp)
	for index, _ := range inp {
		out[index] = out[index] + 1
	}
	return string(out)
}

func decrypt(inp string) string {
	out := []rune(inp)
	for index, _ := range inp {
		out[index] = out[index] - 1
	}
	return string(out)
}

func sortArray(books []Book) []Book {
	sort.Slice(books, func(i, j int) bool {
		return books[i].ID < books[j].ID
	})
	return books
}

func createBooks(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	//	body, err := ioutil.ReadAll(r.Body)
	var book []Book
	_ = json.NewDecoder(r.Body).Decode(&book)

	book = sortArray(book)

	count := 0

	for _, item := range book {
		item.Title = strings.TrimSpace(item.Title)
		item.Isbn = strings.TrimSpace(item.Isbn)
		item.Author.Firstname = strings.TrimSpace(item.Author.Firstname)
		if item.Isbn == "" {
			log.Print("Isbn is empty!")
		}

		if item.Title == "" {
			log.Println("Title is Empty!")
		}

		if item.Author.Firstname == "" {
			log.Println("Firstname of author is empty!")
		}

		countMatchISBN := -1
		searchString := item.Isbn

		fmt.Println("Started for record: ", count)
		for _, item := range book {
			if item.Isbn == searchString {
				countMatchISBN++
			}
			//	fmt.Println("ISBN duplicates for %s : %d ", searchString, countMatchISBN)
		}
		//	fmt.Println("ISBN duplicates for " + searchString + ": " + countMatchISBN)
		count++
		item.Isbn = encrypt(item.Title)
		item.Title = encrypt(item.Title)
		item.Author.Firstname = encrypt(item.Author.Firstname)
		item.Author.Lastname = encrypt(item.Author.Lastname)
	}

	for _, item := range book {
		item.Isbn = encrypt(item.Isbn)
		item.Title = encrypt(item.Title)
		item.Author.Firstname = encrypt(item.Author.Firstname)
		item.Author.Lastname = encrypt(item.Author.Lastname)

	}

	for _, item := range book {
		item.Isbn = decrypt(item.Isbn)
		item.Isbn = decrypt(item.Isbn)
		item.Title = decrypt(item.Title)
		item.Title = decrypt(item.Title)
		item.Author.Firstname = decrypt(item.Author.Firstname)
		item.Author.Firstname = decrypt(item.Author.Firstname)
		item.Author.Lastname = decrypt(item.Author.Lastname)
		item.Author.Lastname = decrypt(item.Author.Lastname)

	}
	json.NewEncoder(w).Encode(book)
}

func main() {

	// Init router
	r := mux.NewRouter()

	// Route Handlers = FOr establishing end points for API's
	r.HandleFunc("/api/books", createBooks).Methods("POST")

	//Setting up server and making it listen to port
	log.Fatal(http.ListenAndServe(":8080", r))
}
