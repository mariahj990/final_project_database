import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy import text


# Run from inside final_project_database/final_project_database 

history_csv_path = "datasource_csvs/to_read.csv" # python p2_starter_code\src\main\resources\import_book_data.py
#python3 p2_starter_code/src/main/resources/import_book_data.py for mac

engine = create_engine("mysql+pymysql://root:mysqlpass@localhost:33306/cs4370_final_library")

# CSVs coming from decomposition of wide dataset

# Users
users = pd.read_csv("datasource_csvs/users.csv")
print(users.head())
users.to_sql("user", engine, if_exists='append', index=False)

# Books
books = pd.read_csv("datasource_csvs/books.csv")
print(books.head())
books.to_sql("book", engine, if_exists='append', index=False)

# Ratings
ratings = pd.read_csv("datasource_csvs/ratings.csv")
print(ratings.head())
ratings.to_sql("ratings", engine, if_exists='append', index=False)

# CSV coming from external table: to_read 
history = pd.read_csv(history_csv_path)
# Print the first 5 rows of the DataFrame
print(history.head())
history.to_sql("history", engine, if_exists='append', index=False)

# CSV coming from external table: bookId -> genre 
book_genres = pd.read_csv("datasource_csvs/book_to_genre.csv")
print(book_genres.head())
book_genres.to_sql("book_to_genre", engine, if_exists='append', index=False)

# CSV coming from external table: genre -> genre category (manual clustering)
genre_category = pd.read_csv("datasource_csvs/genre_category.csv")
print(genre_category)
genre_category.to_sql("genre_category", engine, if_exists='append', index=False)

# populating the count of genre preferences from historical user data. 
with engine.begin() as conn:
    # Compute aggregated counts per user / genre category
    conn.execute(text("""
        INSERT INTO user_genre_count (userId, genreCategoryName, numBooks)
        SELECT 
            h.userId,
            gc.genreCategoryName,
            COUNT(*) as numBooks
        FROM history h
        JOIN book_to_genre bg ON h.bookId = bg.bookId
        JOIN genre_category gc ON bg.genreName = gc.genreName
        GROUP BY h.userId, gc.genreCategoryName;
    """))