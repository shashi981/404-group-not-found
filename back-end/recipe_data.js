const https = require("https");
const mysql = require("mysql2")
const fs = require("fs")

const baseurl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

const startChar = 'a'.charCodeAt(0)
const endChar = 'z'.charCodeAt(0)

const searchTerms = [];

for (let charCode = startChar; charCode <= endChar; charCode++) {
  const char = String.fromCharCode(charCode);
  searchTerms.push(char)
}

const con = mysql.createConnection({
  host: "localhost",
  port: "3306",
  user: "404GroupNotFound",
  password: "404Group",
  database: "grocerymanger"
});


//ChatGPT usage: Yes
function fetchDataForSearchTerm(searchTerm) {
  const url = baseurl + searchTerm

  https.get(url, (response) => {
    let data = ""

    response.on('data', (chunk) => {
    data += chunk;
    });

    response.on("end", () => {
      try {
        const jsonData = JSON.parse(data)
        const filename = `output_${searchTerm}.json`;
        saveDataToFile(jsonData, filename);
        
        insertDataIntoDatabase(jsonData);
      } catch (error) {
        console.error('Error parsing JSON:', error)
      }
    });
  }).on('error', (error) => {
    console.error('Error making the request:', error)
  })
}


//ChatGPT usage: Yes
async function insertDataIntoDatabase(jsonData) {
  
    try{
        jsonData.meals.forEach(async (meal) => {
            const recipeInfoQuery = `INSERT IGNORE INTO RECIPE_INFO (Rname, Category, RID, Instruction, YoutubeLink) VALUES (?, ?, ?, ?, ?);`
            const [results] = await con.promise().query(recipeInfoQuery, [meal.strMeal, meal.strCategory, meal.idMeal, meal.strInstructions, meal.strYoutube])

            console.log('Inserted into RECIPE_INFO:', results)

            for (let i = 1; i <= 20; i++) {
                const ingredientKey = `strIngredient${i}`
                const measureKey = `strMeasure${i}`

                if (meal[ingredientKey] !== "") {
                    const recipeQuery = `INSERT IGNORE INTO RECIPE (RID, Ingredient, Amount) VALUES (?, ?, ?);`
                    const [results] = await con.promise().query(recipeQuery, [meal.idMeal, meal[ingredientKey], meal[measureKey]])

                    console.log('Inserted into RECIPE:', results)
                }
            }
        })

    }catch(error){
        console.error('Error inserting into RECIPE:', error)
    }
}

//save the content as file to ensure its extracted correctly enable when needed
//ChatGPT usage: Yes
/*
function saveDataToFile(jsonData, filename) {
    const jsonStr = JSON.stringify(jsonData, null, 2)
  
    const filePath = filename
  
    fs.writeFile(filePath, jsonStr, (err) => {
      if (err) {
        console.error('Error saving data to the file:', err)
      } else {
        console.log('JSON data has been saved to', filePath)
      }
    })
  }

searchTerms.forEach((searchTerm) => {
    fetchDataForSearchTerm(searchTerm)
})*/