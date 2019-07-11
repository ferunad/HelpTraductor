/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.example.fch.helptranslate;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

//actividad de la api de google para reproducir los videos en pantlala completa


public class FullscreenDemoActivity extends YouTubeFailureRecoveryActivity implements
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener,
    YouTubePlayer.OnFullscreenListener {

  private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
      ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
  private LinearLayout baseLayout;
  private YouTubePlayerView playerView;
  private YouTubePlayer player;
  private Button fullscreenButton,compartir;
  private View otherViews;
  private Button botonRegresar;

  private boolean fullscreen;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fullscreen_demo);

    //conexion con el html
    baseLayout = (LinearLayout) findViewById(R.id.layout);
    playerView = (YouTubePlayerView) findViewById(R.id.player);
    fullscreenButton = (Button) findViewById(R.id.fullscreen_button);
    otherViews = findViewById(R.id.other_views);
    botonRegresar=findViewById(R.id.botonVolver);
    compartir=findViewById(R.id.botonCompartir);

    doLayout();

    //listener del boton de pantalla completa
    fullscreenButton.setOnClickListener(this);

    playerView.initialize(ClaveParaConexion.DEVELOPER_KEY, this);

    botonRegresar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    compartir.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        final String Url=getIntent().getExtras().getString("Url");
        Bundle nombreVideo=new Bundle();
        String linkVideo="https://www.youtube.com/watch?v="+Url;
        //ingresamos los datos en el bundle
        nombreVideo.putString("url",linkVideo);
        //cambiamos de activity y enviamos el bundle
        Intent vista=new Intent(FullscreenDemoActivity.this,EnviarCorreo.class);
        vista.putExtras(nombreVideo);
        startActivity(vista);

      }
    });
  }

  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
      boolean wasRestored) {
    this.player = player;
    // Specify that we want to handle fullscreen behavior ourselves.
    player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
    player.setOnFullscreenListener(this);
    if (!wasRestored) {
      final String Url=getIntent().getExtras().getString("Url");
      player.cueVideo(Url);
    }
  }

  @Override
  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    return playerView;
  }

  @Override
  public void onClick(View v) {
    player.setFullscreen(!fullscreen);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    int controlFlags = player.getFullscreenControlFlags();
    if (isChecked) {
      // If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE, your activity's normal UI
      // should never be laid out in landscape mode (since the video will be fullscreen whenever the
      // activity is in landscape orientation). Therefore you should set the activity's requested
      // orientation to portrait. Typically you would do this in your AndroidManifest.xml, we do it
      // programmatically here since this activity demos fullscreen behavior both with and without
      // this flag).
      setRequestedOrientation(PORTRAIT_ORIENTATION);
      controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
      controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
    }
    player.setFullscreenControlFlags(controlFlags);
  }

  private void doLayout() {
    LayoutParams playerParams =
        (LayoutParams) playerView.getLayoutParams();
    if (fullscreen) {
      // When in fullscreen, the visibility of all other views than the player should be set to
      // GONE and the player should be laid out across the whole screen.
      playerParams.width = LayoutParams.MATCH_PARENT;
      playerParams.height = LayoutParams.MATCH_PARENT;

      otherViews.setVisibility(View.GONE);
    } else {
      // This layout is up to you - this is just a simple example (vertically stacked boxes in
      // portrait, horizontally stacked in landscape).
      otherViews.setVisibility(View.VISIBLE);
      ViewGroup.LayoutParams otherViewsParams = otherViews.getLayoutParams();
      if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        playerParams.width = otherViewsParams.width = 0;
        playerParams.height = WRAP_CONTENT;
        otherViewsParams.height = MATCH_PARENT;
        playerParams.weight = 1;
        baseLayout.setOrientation(LinearLayout.HORIZONTAL);
      } else {
        playerParams.width = otherViewsParams.width = MATCH_PARENT;
        playerParams.height = WRAP_CONTENT;
        playerParams.weight = 0;
        otherViewsParams.height = 0;
        baseLayout.setOrientation(LinearLayout.VERTICAL);
      }

    }
  }



  @Override
  public void onFullscreen(boolean isFullscreen) {
    fullscreen = isFullscreen;
    doLayout();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    doLayout();
  }

}
