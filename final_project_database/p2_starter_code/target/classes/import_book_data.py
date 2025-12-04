import pandas as pd
from sqlalchemy import create_engine

to_read_csv_path = "datasource_csvs/to_read.csv" # python p2_starter_code\src\main\resources\import_book_data.py

engine = create_engine("mysql+pymysql://root:mysqlpass@localhost:33306/cs4370_final_library")

df = pd.read_csv(to_read_csv_path)

# Print the first 5 rows of the DataFrame
print(df.head())

df.to_sql("to_read", engine, if_exists='replace', index=False)
