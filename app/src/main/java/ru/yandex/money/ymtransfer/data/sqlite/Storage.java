package ru.yandex.money.ymtransfer.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.DatabaseCompartment;
import ru.yandex.money.ymtransfer.App;
import ru.yandex.money.ymtransfer.data.model.Transfer;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class Storage {

    private SQLiteOpenHelper mOpenHelper;
    private SQLiteDatabase mDatabase;
    private DatabaseCompartment mDataCompartment;

    private static Storage sInstance;
    private static final Object INIT_LOCK = new Object();

    private Storage(Context context) {
        initCupboardSettings();

        mOpenHelper = new YMSqliteOpenHelper(context);
        mDatabase = mOpenHelper.getWritableDatabase();
        mDataCompartment = cupboard().withDatabase(mDatabase);
    }

    private void initCupboardSettings() {
        Cupboard cupboard = new CupboardBuilder()
                .useAnnotations()
                .build();

        cupboard.register(Transfer.class);

        CupboardFactory.setCupboard(cupboard);
    }

    public static Storage get() {
        if (sInstance == null) {
            synchronized (INIT_LOCK) {
                if (sInstance == null) {
                    sInstance = new Storage(App.getInstance());
                }
            }
        }
        return sInstance;
    }

    public long put(Object entity) {
        return mDataCompartment.put(entity);
    }

    public void put(Collection<?> entities) {
        mDataCompartment.put(entities);
    }

    public int clearTable(Class classId) {
        return mDataCompartment.delete(classId, null);
    }

    public boolean delete(Object entity) {
        return mDataCompartment.delete(entity);
    }

    public boolean delete(Class<?> className, long id) {
        return mDataCompartment.delete(className, id);
    }

    public int delete(Class<?> className, String selection, String... args) {
        return mDataCompartment.delete(className, selection, args);
    }

    public <T> List<T> get(Class<T> className) {
        return mDataCompartment.query(className).list();
    }

    public <T> T get(Class<T> className, long id) {
        return mDataCompartment.get(className, id);
    }

    public <T> List<T> getWithSelectionList(Class<T> className, String selection, String... args) {
        return mDataCompartment.query(className).withSelection(selection, args).list();
    }

    public <T> T getWithSelectionObject(Class<T> className, String selection, String... args) {
        return mDataCompartment.query(className).withSelection(selection, args).get();
    }

    public <T> DatabaseCompartment.QueryBuilder<T> getQuery(Class<T> className) {
        return mDataCompartment.query(className);
    }

    public int update(Class<?> entityClass, ContentValues values,
                      String selection, String... selectionArgs) {
        return mDataCompartment.update(entityClass, values, selection, selectionArgs);
    }

    public void beginTransaction() {
        mDatabase.beginTransactionNonExclusive();
    }

    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    public void endTransaction() {
        mDatabase.endTransaction();
    }

}
