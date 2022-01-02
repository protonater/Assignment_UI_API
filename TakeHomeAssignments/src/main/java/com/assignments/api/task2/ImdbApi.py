# Step 1: Use the IMDbPY package to get movie data from IMDB database.
import imdb
import json

# Step 2: Get the list of bottom 100 movies (use the builtin method - get_bottom100_movies())
ia = imdb.IMDb()
bottom = ia.get_bottom100_movies()
movieList = []

for movie in bottom:
    movieDict = {}
    # Step 3: Get director name for each movies (use the builtin method - get_movie())
    movieInfo = ia.get_movie(movie.movieID)
    # Step 4: Collect only the movie names and director names (in ascending order by director names)
    movieDict['movie_name'] = movie.data['title']
    movieDict['director'] = movieInfo.data['director'][0].data['name']
    movieList.append(movieDict)

# Step 5: Pretty-print the data in json and store in a json file
movieListJson = json.dumps(sorted(movieList, key=lambda item: item['director']), indent=2);
res = open('result.json', mode='w')
res.write(movieListJson)

