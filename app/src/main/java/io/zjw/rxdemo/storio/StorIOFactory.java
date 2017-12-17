package io.zjw.rxdemo.storio;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import io.zjw.rxdemo.models.StockUpdate;

/**
 * Created by Lincoln on 2017/12/17.
 */

public class StorIOFactory {
    private static StorIOSQLite INSTANCE;

    public static synchronized StorIOSQLite get(Context context) {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new StorIODbHelper(context))
                .addTypeMapping(StockUpdate.class, SQLiteTypeMapping.<StockUpdate>builder()
                        .putResolver(new StockUpdatePutResolver())
                        .getResolver(createGetResolver())
                        .deleteResolver(createDeleteResolver())
                        .build())
                .build();
        return INSTANCE;
    }

    private static GetResolver<StockUpdate> createGetResolver() {
        return new DefaultGetResolver<StockUpdate>() {
            @NonNull
            @Override
            public StockUpdate mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
                return null;
            }
        };
    }

    private static DeleteResolver<StockUpdate> createDeleteResolver() {
        return new DefaultDeleteResolver<StockUpdate>() {
            @NonNull
            @Override
            protected DeleteQuery mapToDeleteQuery(@NonNull StockUpdate object) {
                return null;
            }
        };
    }

}
