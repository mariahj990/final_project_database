import pandas as pd
from sqlalchemy import create_engine

to_read_csv_path = "datasource_csvs/to_read.csv"

engine = create_engine("mysql+pymysql://root:mysqlpass@localhost:33306/cs4370_final_libary")

df = pd.read_csv(to_read_csv_path)

# Print the first 5 rows of the DataFrame
print(df.head())

df.to_sql("to_read", engine, if_exists='replace', index=False)
