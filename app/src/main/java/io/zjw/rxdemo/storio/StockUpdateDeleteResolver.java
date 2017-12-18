package io.zjw.rxdemo.storio;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import io.zjw.rxdemo.models.StockUpdate;

/**
 * Created by mega on 2017/12/18.
 */

public class StockUpdateDeleteResolver extends DefaultDeleteResolver<StockUpdate> {
    @NonNull
    @Override
    protected DeleteQuery mapToDeleteQuery(@NonNull StockUpdate object) {
        return DeleteQuery.builder()
                .table(StockUpdateTable.TABLE)
                .where(StockUpdateTable.Columns.ID + " = ?")
                .whereArgs(object.getId())
                .build();
    }
}
