import pandas as pd
from sqlalchemy import create_engine

amazon_csv_path = "Amazon.csv"

engine = create_engine("mysql+pymysql://root:mysqlpass@localhost:33306/cs4370_final_amazon")

df = pd.read_csv(amazon_csv_path)

# Print the first 5 rows of the DataFrame
print(df.head())

df.to_sql("amazon_sales", engine, if_exists='replace', index=False)
