import pandas as pd
from sqlalchemy import create_engine

book_data_csv_path = "book_data.csv"

engine = create_engine("mysql+pymysql://root:mysqlpass@localhost:33306/cs4370_final_libary")

df = pd.read_csv(book_data_csv_path)

# Print the first 5 rows of the DataFrame
print(df.head())

df.to_sql("book_data", engine, if_exists='replace', index=False)

#To use: python 