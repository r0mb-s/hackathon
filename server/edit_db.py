import psycopg2

def get_con():
        return psycopg2.connect(host="localhost", database="hackathon", user='hackathon', password='arduino')
        

def get_counters():
        conn = get_con()
        cur = conn.cursor()
        cur.execute('CREATE TABLE IF NOT EXISTS data (id integer PRIMARY KEY, percentage float NOT NULL);')
        cur.execute('CREATE TABLE IF NOT EXISTS coordinates (id integer PRIMARY KEY, longitude float NOT NULL, latitude float NOT NULL );')
        conn.commit()

        cur.execute('SELECT id FROM data;')
        result = cur.fetchall()

        dictionar = {}
        masini = {}

        for row in result:
                dictionar[row[0]] = 0
                masini[row[0]] = 0

        cur.close()
        conn.close()

        return dictionar, masini

def get_coordinates_and_percentage():
        conn = get_con()
        cur = conn.cursor()
        cur.execute('CREATE TABLE IF NOT EXISTS data (id integer PRIMARY KEY, percentage float NOT NULL);')
        cur.execute('CREATE TABLE IF NOT EXISTS coordinates (id integer PRIMARY KEY, longitude float NOT NULL, latitude float NOT NULL );')
        conn.commit()

        cur.execute('SELECT c.id, longitude, latitude, percentage FROM coordinates c JOIN data d ON c.id = d.id;')
        result = cur.fetchall()

        dictionar = {}

        for row in result:
                if result:
                        longitude = row[1]
                        latitude = row[2]
                        percentage = row[3]
                        dictionar[row[0]] = [latitude, longitude, percentage]
                        

        cur.close()
        conn.close()

        return dictionar

def functie(id, p):
        conn = get_con()
        cur = conn.cursor()
        cur.execute('CREATE TABLE IF NOT EXISTS data (id integer PRIMARY KEY, percentage float NOT NULL);')
        cur.execute('CREATE TABLE IF NOT EXISTS coordinates (id integer PRIMARY KEY, longitude float NOT NULL, latitude float NOT NULL );')

        cur.execute('INSERT INTO data (id, percentage) VALUES (%s, %s) ON CONFLICT (id) DO UPDATE SET percentage = %s;',
                        (id, p, p))
        conn.commit()

        cur.close()
        conn.close()