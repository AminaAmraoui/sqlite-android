package com.example.sqliteapplication;

// ---------------------------------------------------------------------------
// IMPORTS : chaque classe importée est expliquée ici
// ---------------------------------------------------------------------------

// ContentValues : sorte de "dictionnaire" (clé = nom de colonne, valeur = donnée)
// utilisé pour passer les valeurs à insérer ou à modifier dans la base.
import android.content.ContentValues;

// Context : donne accès à l'environnement de l'application (fichiers, ressources...).
// SQLite en a besoin pour savoir OÙ stocker le fichier de la base sur le téléphone.
import android.content.Context;

// Cursor : objet qui permet de parcourir, ligne par ligne, le résultat d'une requête SELECT.
import android.database.Cursor;

// SQLiteDatabase : représente la base de données ouverte. C'est par elle que l'on
// exécute les requêtes (insert, delete, update, query, execSQL...).
import android.database.sqlite.SQLiteDatabase;

// SQLiteOpenHelper : classe abstraite fournie par Android qui gère la CRÉATION et
// la MISE À JOUR (changement de version) de la base. On en hérite.
import android.database.sqlite.SQLiteOpenHelper;

// Les trois imports suivants ne sont pas utilisés dans cette classe
// (Log, Serializable, List). Un IDE comme Android Studio les grise : on peut
// les supprimer. Garder des imports inutiles alourdit la lecture du code.
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentOpenHelper : classe d'accès à la base SQLite des étudiants.
 *
 * Elle a deux rôles :
 *   1. Créer / mettre à jour la structure de la base (grâce à l'héritage de
 *      SQLiteOpenHelper : méthodes onCreate et onUpgrade).
 *   2. Fournir les opérations CRUD sur la table "student" :
 *        - Create : addStudent()
 *        - Read   : getAllStudents(), getStudent()
 *        - Update : updateStudent()
 *        - Delete : deleteStudent()
 *
 * Idée à retenir : le reste de l'application (les Activities) ne contient
 * AUCUNE requête SQL. Elle appelle simplement les méthodes de cette classe et
 * manipule des objets Student. Cela sépare l'interface de l'accès aux données.
 *
 * Doc officielle :
 * https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
 */
public class StudentOpenHelper extends SQLiteOpenHelper {

    // -----------------------------------------------------------------------
    // CONSTANTES : nom et structure de la base
    // -----------------------------------------------------------------------

    // Version de la base. Elle sert à gérer l'évolution du schéma : si vous
    // changez la structure de la table (ajout d'une colonne par exemple), vous
    // augmentez ce numéro (1 -> 2). Android détecte alors que la base installée
    // sur le téléphone est plus ancienne et appelle onUpgrade().
    private static final int DATABASE_VERSION = 1;

    // Nom du fichier de la base de données, créé dans le stockage privé de
    // l'application (les autres applications ne peuvent pas le lire).
    private static final String DATABASE_NAME = "studentDB";

    // Nom de la table. (Le commentaire d'origine disait "Contacts table name" :
    // c'est un reste de copier-coller d'un autre exemple, la table contient bien
    // des étudiants.)
    private static final String TABLE_STUDENT = "student";

    // Noms des colonnes. Les déclarer en constantes évite les fautes de frappe
    // dans les requêtes : le compilateur signale une constante inconnue, alors
    // qu'il ne détecte pas une faute dans une chaîne "nmae".
    // Elles sont "public" pour pouvoir être réutilisées par d'autres classes.
    public static final String ID = "ID";
    public static final String NAME = "name";
    public static final String NOTE = "note";

    // Alias public du nom de la table, pour que les autres classes puissent
    // y accéder sans dupliquer la chaîne "student".
    public static final String TABLE_NAME=TABLE_STUDENT;

    // -----------------------------------------------------------------------
    // CONSTRUCTEUR
    // -----------------------------------------------------------------------

    // Paramètres de super(...) :
    //   - context           : le contexte de l'application
    //   - DATABASE_NAME     : le nom du fichier de la base
    //   - null              : fabrique de curseurs personnalisée (inutile ici)
    //   - DATABASE_VERSION  : la version du schéma
    //
    // IMPORTANT : à ce stade, la base n'est PAS encore créée. Elle ne l'est
    // qu'au premier appel de getWritableDatabase() ou getReadableDatabase().
    public StudentOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // -----------------------------------------------------------------------
    // CRÉATION DE LA BASE
    // -----------------------------------------------------------------------

    // onCreate() est appelée automatiquement par Android UNE SEULE FOIS : la
    // première fois que la base est ouverte et que le fichier n'existe pas encore.
    // On y crée les tables.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // On construit la requête SQL par concaténation. Une fois assemblée, elle donne :
        //   CREATE TABLE student(ID Integer PRIMARY KEY AUTOINCREMENT,
        //                        name TEXT, note Number(2,2))
        //
        // - "Integer PRIMARY KEY AUTOINCREMENT" : l'identifiant est un entier
        //   généré automatiquement (1, 2, 3...) et unique pour chaque étudiant.
        // - "TEXT"          : le nom est une chaîne de caractères.
        // - "Number(2,2)"   : attention, SQLite n'applique PAS cette précision.
        //   SQLite n'a que 5 "affinités" de type (INTEGER, TEXT, BLOB, REAL,
        //   NUMERIC) et ignore la taille (2,2). Ici la colonne est de type
        //   NUMERIC. Pour une note décimale, "REAL" serait plus clair.
        //   Voir : https://www.sqlite.org/datatype3.html
        String CREATE_TABLE = "CREATE TABLE " + TABLE_STUDENT + "(" 
                + ID + " Integer PRIMARY KEY AUTOINCREMENT," + NAME + " TEXT,"
                + NOTE + " Number(2,2)" + ")";

        // execSQL() exécute une requête SQL qui ne renvoie pas de résultat
        // (CREATE, DROP, ALTER...). Pour un SELECT, on utilise rawQuery() ou query().
        db.execSQL(CREATE_TABLE);
    }

    // -----------------------------------------------------------------------
    // MISE À JOUR DU SCHÉMA
    // -----------------------------------------------------------------------

    // onUpgrade() est appelée quand DATABASE_VERSION (dans le code) est PLUS GRANDE
    // que la version de la base déjà présente sur le téléphone.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Stratégie la plus simple : supprimer l'ancienne table...
        // "IF EXISTS" évite une erreur si la table n'existe pas.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);

        // ...puis la recréer avec la nouvelle structure.
        // ATTENTION : cette stratégie EFFACE TOUTES LES DONNÉES existantes.
        // Acceptable pour un TP. Dans une vraie application, on utilise des
        // migrations (ALTER TABLE ...) pour conserver les données des utilisateurs.
        onCreate(db);
    }

    // -----------------------------------------------------------------------
    // CREATE : ajouter un étudiant
    // -----------------------------------------------------------------------

    // Ajoute un étudiant dans la table à partir d'un objet Student.
    public void addStudent(Student s) {
        // getWritableDatabase() ouvre la base en LECTURE/ÉCRITURE. C'est ici que
        // onCreate() est déclenchée la toute première fois.
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues associe chaque colonne à sa valeur.
        // On ne met PAS l'ID : la base le génère elle-même (AUTOINCREMENT).
        ContentValues values = new ContentValues();
        values.put(NAME, s.getName());
        values.put(NOTE, s.getNote());

        // insert(table, nullColumnHack, valeurs) :
        //   - le 2e paramètre (null) ne sert que si "values" est vide : on peut
        //     l'ignorer pour l'instant.
        // insert() renvoie l'ID de la nouvelle ligne (ou -1 en cas d'erreur).
        // Ici cette valeur de retour est ignorée : on ne saura pas si l'insertion a échoué.
        // Avantage de insert() avec ContentValues : les valeurs sont échappées
        // automatiquement, on évite l'injection SQL.
        db.insert(TABLE_STUDENT, null, values);

        // Fermeture de la connexion. Ce n'est pas toujours nécessaire : l'objet
        // SQLiteOpenHelper garde la connexion ouverte pour la réutiliser. Mais
        // si on la ferme, il faut être cohérent dans toutes les méthodes.
        db.close(); // Closing database connection
    }

    // -----------------------------------------------------------------------
    // READ : lire tous les étudiants
    // -----------------------------------------------------------------------

    // Renvoie la liste de tous les étudiants de la table.
    public ArrayList<Student> getAllStudents() {
        // Liste vide qui sera remplie puis renvoyée.
        // (Convention Java : une variable commence par une minuscule,
        // on écrirait plutôt "studentList".)
        ArrayList<Student> StudentList = new ArrayList<Student>();

        // Requête SQL qui sélectionne toutes les colonnes de toutes les lignes.
        // Aucune donnée saisie par l'utilisateur n'entre dans cette requête :
        // il n'y a donc pas de risque d'injection SQL ici.
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENT;

        // Pour une simple lecture, getReadableDatabase() suffirait. Ici on
        // demande l'écriture alors qu'on ne modifie rien.
        SQLiteDatabase db = this.getWritableDatabase();

        // rawQuery(sql, args) exécute un SELECT écrit à la main.
        // Le 2e paramètre sert à fournir les valeurs des "?" (aucun ici -> null).
        // Le résultat est un Cursor, positionné AVANT la première ligne.
        Cursor cursor = db.rawQuery(selectQuery, null);

        // moveToFirst() place le curseur sur la première ligne.
        // Elle renvoie false si le résultat est vide : on ne rentre alors pas
        // dans le if et la liste renvoyée est simplement vide.
        if (cursor.moveToFirst()) {
            do {
                // On crée un objet Student pour chaque ligne lue.
                Student std = new Student();

                // Les colonnes se lisent par leur POSITION, dans l'ordre de
                // création de la table (SELECT * respecte cet ordre) :
                //   0 = ID, 1 = name, 2 = note
                // getString(i) lit la colonne i sous forme de texte, puis on
                // convertit en nombre avec parseInt / parseDouble.
                // Plus robuste : cursor.getInt(...) / cursor.getDouble(...) avec
                // cursor.getColumnIndexOrThrow(ID) pour ne pas dépendre de la position.
                std.setID(Integer.parseInt(cursor.getString(0)));
                std.setName(cursor.getString(1));
                std.setNote(Double.parseDouble(cursor.getString(2)));

                // On ajoute l'étudiant à la liste.
                StudentList.add(std);

                // moveToNext() passe à la ligne suivante, et renvoie false
                // quand il n'y en a plus : la boucle s'arrête alors.
            } while (cursor.moveToNext());
        }

        // Ici, le curseur n'est jamais fermé (cursor.close() manquant). Un curseur
        // non fermé garde des ressources en mémoire : c'est une fuite
        // ("cursor leak") que Android signale dans le Logcat.
        // Bonne pratique : appeler cursor.close() avant le return.

        // On renvoie la liste complète.
        return StudentList;
    }

    // -----------------------------------------------------------------------
    // DELETE : supprimer un étudiant
    // -----------------------------------------------------------------------

    // Supprime l'étudiant dont l'identifiant est "id".
    public void deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // delete(table, clauseWhere, argumentsDuWhere)
        //   - ID + " = ?"  donne  "ID = ?"  : le "?" est un PARAMÈTRE.
        //   - new String[]{...} fournit la valeur qui remplace le "?".
        // Pourquoi ne pas écrire directement "ID = " + id ? Avec les "?", SQLite
        // traite la valeur comme une simple donnée et jamais comme du code SQL :
        // c'est la protection contre l'injection SQL. À utiliser systématiquement.
        // Les arguments sont toujours des String : d'où String.valueOf(id).
        // delete() renvoie le nombre de lignes supprimées (ici ignoré).
        db.delete(TABLE_STUDENT, ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // -----------------------------------------------------------------------
    // READ : lire UN étudiant par son identifiant
    // -----------------------------------------------------------------------

    // Cette méthode n'a pas de modificateur d'accès : elle est "package-private",
    // donc visible uniquement dans le package com.example.sqliteapplication.
    // Les autres méthodes sont "public".
    Student getStudent(int id) {
        // Pour une lecture seule, getReadableDatabase() est le bon choix.
        SQLiteDatabase db = this.getReadableDatabase();

        // query(...) construit le SELECT à partir de ses morceaux, sans écrire
        // le SQL à la main. Les 8 paramètres sont dans l'ordre :
        //   1. la table                  : TABLE_STUDENT
        //   2. les colonnes à lire       : { ID, NAME, NOTE }
        //   3. la clause WHERE           : ID + "=?"
        //   4. les valeurs des "?"       : { String.valueOf(id) }
        //   5. groupBy                   : null (pas de regroupement)
        //   6. having                    : null
        //   7. orderBy                   : null (pas de tri)
        //   8. limit                     : null (pas de limite)
        // Équivalent SQL :  SELECT ID, name, note FROM student WHERE ID = ?
        Cursor cursor = db.query(TABLE_STUDENT, new String[] { ID,
                        NAME, NOTE }, ID + "=?",
                new String[] { String.valueOf(id) }, null,
                null, null, null);

        // query() ne renvoie jamais null en pratique, ce test est donc superflu.
        // moveToFirst() place le curseur sur la première (et unique) ligne.
        if (cursor != null)
            cursor.moveToFirst();

        // Les colonnes lues sont dans l'ordre demandé à query() : 0 = ID, 1 = name, 2 = note.
        // On construit un Student avec le constructeur (nom, note).
        // Remarques :
        //   - l'ID n'est pas recopié dans l'objet renvoyé (on a pourtant lu la colonne 0) ;
        //   - si aucun étudiant n'a cet id, moveToFirst() renvoie false et
        //     getString(1) lève une exception (CursorIndexOutOfBoundsException) qui
        //     fait planter l'application. Il faudrait tester le résultat de
        //     moveToFirst() et renvoyer null dans ce cas ;
        //   - le curseur n'est pas fermé (cursor.close()).
        Student std = new Student(cursor.getString(1),Double.parseDouble( cursor.getString(2)));

        // On renvoie l'étudiant trouvé.
        return std;
    }

    // -----------------------------------------------------------------------
    // UPDATE : modifier un étudiant
    // -----------------------------------------------------------------------

    // Met à jour la ligne de l'étudiant "std", identifiée par son ID.
    // Renvoie le nombre de lignes modifiées (1 si trouvé, 0 si l'ID n'existe pas),
    // ce qui permet à l'appelant de vérifier que la modification a bien eu lieu.
    public int updateStudent(Student std) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Les nouvelles valeurs à enregistrer.
        ContentValues values = new ContentValues();

        // On remet ici l'ID dans les valeurs : c'est inutile, car il sert déjà
        // de critère dans le WHERE ci-dessous et on ne le change pas. On pourrait
        // supprimer cette ligne.
        values.put(ID,std.getID());
        values.put(NAME, std.getName());
        values.put(NOTE, std.getNote());

        // update(table, nouvellesValeurs, clauseWhere, argumentsDuWhere)
        // Équivalent SQL :  UPDATE student SET ID=?, name=?, note=? WHERE ID = ?
        // Sans clause WHERE, TOUTES les lignes de la table seraient modifiées :
        // ne jamais l'oublier.
        // Comme pour delete(), le "?" protège contre l'injection SQL.
        // Ici la base n'est pas fermée : c'est incohérent avec addStudent() et
        // deleteStudent() qui appellent db.close().
        return db.update(TABLE_STUDENT, values, ID + " = ?",
                new String[] { String.valueOf(std.getID()) });
    }

    /*
     * ------------------------------------------------------------------------
     *  RÉCAPITULATIF : méthodes SQLiteDatabase utilisées dans cette classe
     * ------------------------------------------------------------------------
     *  execSQL(sql)                      -> requêtes sans résultat (CREATE, DROP)
     *  insert(table, null, values)       -> INSERT        (renvoie l'ID, -1 si erreur)
     *  rawQuery(sql, args)               -> SELECT écrit à la main (renvoie un Cursor)
     *  query(table, cols, where, args..) -> SELECT construit par morceaux (renvoie un Cursor)
     *  update(table, values, where, args)-> UPDATE        (renvoie le nb de lignes modifiées)
     *  delete(table, where, args)        -> DELETE        (renvoie le nb de lignes supprimées)
     *
     *  Références officielles :
     *   - SQLiteDatabase : https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase
     *   - Cursor         : https://developer.android.com/reference/android/database/Cursor
     *   - ContentValues  : https://developer.android.com/reference/android/content/ContentValues
     *   - Guide SQLite   : https://developer.android.com/training/data-storage/sqlite
     * ------------------------------------------------------------------------
     */

}
