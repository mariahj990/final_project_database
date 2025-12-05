import pandas as pd
from sqlalchemy import create_engine

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

