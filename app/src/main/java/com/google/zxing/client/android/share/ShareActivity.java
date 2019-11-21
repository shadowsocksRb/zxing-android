/*
 * Copyright (C) 2008 ZXing authors
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

package com.google.zxing.client.android.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.R;

/**
 * Barcode Scanner can share data like contacts and bookmarks by displaying a QR Code on screen,
 * such that another user can scan the barcode with their phone.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ShareActivity extends Activity {

  private final View.OnKeyListener textListener = (view, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
      String text = ((TextView) view).getText().toString();
      if (!text.isEmpty()) {
        launchSearch(text);
      }
      return true;
    }
    return false;
  };

  private void launchSearch(String text) {
    Intent intent = new Intent(Intents.Encode.ACTION);
    intent.addFlags(Intents.FLAG_NEW_DOC);
    intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
    intent.putExtra(Intents.Encode.DATA, text);
    intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
    startActivity(intent);
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.share);

    TextView textView = findViewById(R.id.share_text_view);
    textView.setOnKeyListener(textListener);
    textView.requestFocus();
  }

}