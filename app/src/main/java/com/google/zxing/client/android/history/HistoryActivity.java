/*
 * Copyright 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * The activity for interacting with the scan history.
 */
public final class HistoryActivity extends ListActivity {

  private static final String TAG = HistoryActivity.class.getSimpleName();
  private static final int WRITE_REQUEST_CODE = 3;

  private HistoryManager historyManager;
  private ArrayAdapter<HistoryItem> adapter;
  private CharSequence originalTitle;
  
  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.historyManager = new HistoryManager(this);  
    adapter = new HistoryItemAdapter(this);
    setListAdapter(adapter);
    View listview = getListView();
    registerForContextMenu(listview);
    originalTitle = getTitle();
  }

  @Override
  protected void onResume() {
    super.onResume();
    reloadHistoryItems();
  }

  private void reloadHistoryItems() {
    Iterable<HistoryItem> items = historyManager.buildHistoryItems();
    adapter.clear();
    for (HistoryItem item : items) {
      adapter.add(item);
    }
    setTitle(originalTitle + " (" + adapter.getCount() + ')');
    if (adapter.isEmpty()) {
      adapter.add(new HistoryItem(null, null, null));
    }
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    if (adapter.getItem(position).getResult() != null) {
      Intent intent = new Intent(this, CaptureActivity.class);
      intent.putExtra(Intents.History.ITEM_NUMBER, position);
      setResult(Activity.RESULT_OK, intent);
      finish();
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu,
                                  View v,
                                  ContextMenu.ContextMenuInfo menuInfo) {
    int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
    if (position >= adapter.getCount() || adapter.getItem(position).getResult() != null) {
      menu.add(Menu.NONE, position, position, R.string.history_clear_one_history_text);
    } // else it's just that dummy "Empty" message
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    int position = item.getItemId();
    historyManager.deleteHistoryItem(position);
    reloadHistoryItems();
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (historyManager.hasHistoryItems()) {
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate(R.menu.history, menu);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_history_send:
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "history-" + System.currentTimeMillis() + ".csv");
        startActivityForResult(intent, WRITE_REQUEST_CODE);
        break;
      case R.id.menu_history_clear_text:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_sure);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.button_ok, (dialog, i2) -> {
          historyManager.clearHistory();
          dialog.dismiss();
          finish();
        });
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
      Uri uri = data.getData();
      if (uri == null) return;
      ParcelFileDescriptor parcelFileDescriptor = null;
      try {
        parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "w");
      } catch (FileNotFoundException ignored) {
      }
      if (parcelFileDescriptor == null) return;
      FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
      historyManager.saveHistory(fileDescriptor);
    }
  }
}
