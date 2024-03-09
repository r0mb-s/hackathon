import psycopg2

def get_con():
        return psycopg2.connect(host="localhost", database="hackathon", user='hackathon', password='arduino')

def get_percentage(id):
        conn = get_con()
        cur = conn.cursor()

        cur.execute('SELECT percentage FROM data WHERE id = %s;', (id,))
        result = cur.fetchone()

        if result:
                percentage = result[0]
                return {'id': id, 'percentage': percentage}
        else:
                return {'error': 'No data found for the given ID'}

def functie(id, p):
        conn = get_con()
        cur = conn.cursor()
        
        cur.execute('CREATE TABLE IF NOT EXISTS data (id integer PRIMARY KEY, percentage float NOT NULL);')
        cur.execute('INSERT INTO data (id, percentage) VALUES (%s, %s) ON CONFLICT (id) DO UPDATE SET percentage = %s;',
                        (id, p, p))
        conn.commit()

        cur.close()
        conn.close()