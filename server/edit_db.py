import psycopg2


def functie(id, p):
    conn = psycopg2.connect(
        host="localhost",
        database="hackathon",
        user='hackathon',
        password='arduino')

    cur = conn.cursor()
    
    # Create the table if it doesn't exist
    cur.execute('CREATE TABLE IF NOT EXISTS data (id integer PRIMARY KEY, percentage float NOT NULL);')

    # Use placeholders to avoid SQL injection
    cur.execute('INSERT INTO data (id, percentage) VALUES (%s, %s) ON CONFLICT (id) DO UPDATE SET percentage = %s;',
                (id, p, p))
    
    conn.commit()

    cur.close()
    conn.close()