    package com.example.mediaplayx.services;

    import android.app.Notification;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.app.PendingIntent;

    import android.content.Intent;
    import android.media.MediaPlayer;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;

    import androidx.annotation.NonNull;

    import android.os.Handler;
    import android.os.StrictMode;
    import android.support.v4.media.MediaBrowserCompat.MediaItem;

    import androidx.core.app.NotificationCompat;
    import androidx.media.MediaBrowserServiceCompat;


    import android.support.v4.media.MediaDescriptionCompat;
    import android.support.v4.media.MediaMetadataCompat;
    import android.support.v4.media.session.MediaSessionCompat;
    import android.support.v4.media.session.PlaybackStateCompat;
    import android.util.Log;

    import com.example.mediaplayx.R;
    import com.example.mediaplayx.data.model.Songmodel;
    import com.example.mediaplayx.data.repository.SongRepo;
    import com.example.mediaplayx.ui.view.MainActivity;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;


    public class MyMusicService extends MediaBrowserServiceCompat {
        private static final int NOTIFICATION_ID = 1;

        // Action constants
        private static final String ACTION_PLAY = "com.example.mediaplayx.ACTION_PLAY";
        private static final String ACTION_PAUSE = "com.example.mediaplayx.ACTION_PAUSE";
        private static final String ACTION_NEXT = "com.example.mediaplayx.ACTION_NEXT";
        private static final String ACTION_PREVIOUS = "com.example.mediaplayx.ACTION_PREVIOUS";
        private static final String ACTION_REWIND = "com.example.mediaplayx.ACTION_REWIND";
        private static final String ACTION_FAST_FORWARD = "com.example.mediaplayx.ACTION_FAST_FORWARD";
        private static final String ACTION_STOP = "com.example.mediaplayx.ACTION_STOP";

        private MediaSessionCompat mSession;
        private Notification notification;
        private static final String CHANNEL_ID = "MEDIA_PLAYBACK_CHANNEL";

        private Handler seekBarHandler = new Handler();

        private static final String TAG = MyMusicService.class.getSimpleName();
        private static final String MY_MEDIA_ROOT_ID = "my_media_root_id";
        private MediaPlayer mediaPlayer;
        private PlaybackStateCompat.Builder stateBuilder;

        private SongRepo songRepo;
        private Songmodel getsong;
        private MediaMetadataCompat metadata;

        private int currentPosition;

        private List<Songmodel> allSongs = new ArrayList<>();
        private int currentSongIndex = -1;
        private NotificationManager manager;


        private Runnable updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    updateseeeeek();
                }
                seekBarHandler.postDelayed(this, 1000); // Update every second
            }
        };

        private void startUpdatingSeekBar() {
            seekBarHandler.post(updateSeekBarRunnable);
        }

        // Stop updating the SeekBar when the song is paused or stopped
        private void stopUpdatingSeekBar() {
            seekBarHandler.removeCallbacks(updateSeekBarRunnable);
        }

        @Override
        public void onCreate() {
            super.onCreate();
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());


            mSession = new MediaSessionCompat(this, "MyMusicService");
            setSessionToken(mSession.getSessionToken());
            mSession.setCallback(new MediaSessionCallback());
            mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mediaPlayer = new MediaPlayer();
            songRepo = SongRepo.getInstance(this);
            allSongs = songRepo.getAllSongs();
            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
            mSession.setPlaybackState(stateBuilder.build());
            mSession.setActive(true);
            createNotificationChannel();
        }

        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Media Playback",
                        NotificationManager.IMPORTANCE_LOW
                );
                channel.setDescription("Media playback controls");
                manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    Log.d(TAG, "createNotificationChannel: ");
                    manager.createNotificationChannel(channel);
                }
            }
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });


        }


        private void showNotification( int  playbackState,String title) {
            int playPauseIcon = playbackState == PlaybackStateCompat.STATE_PLAYING ?
                    R.drawable.baseline_pause_circle_24 : R.drawable.baseline_play_circle_24;
            String playPauseAction = playbackState == PlaybackStateCompat.STATE_PLAYING ?ACTION_PAUSE : ACTION_PLAY;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(new NotificationCompat.Action(R.drawable.baseline_arrow_left_24, "Previous", getServicePendingIntent(ACTION_PREVIOUS)))
                    .addAction(new NotificationCompat.Action(playPauseIcon, "Play/Pause", getServicePendingIntent(playPauseAction)))
                    .addAction(new NotificationCompat.Action(R.drawable.baseline_arrow_right_24, "Next", getServicePendingIntent(ACTION_NEXT)))
                    .addAction(new NotificationCompat.Action(R.drawable.baseline_fast_rewind_24, "Rewind", getServicePendingIntent(ACTION_REWIND)))
                    .addAction(new NotificationCompat.Action(R.drawable.baseline_fast_forward_24, "Forward", getServicePendingIntent(ACTION_FAST_FORWARD)))
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mSession.getSessionToken())
                            .setShowActionsInCompactView(1));

            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
            if (manager!= null) {
                manager.notify(NOTIFICATION_ID, notification);
            }

        }
        private PendingIntent getServicePendingIntent(String action) {
            Intent intent = new Intent(this, MyMusicService.class);
            intent.setAction(action);
            return PendingIntent.getService(this, action.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }


        private PendingIntent getContentIntent() {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }


        @Override
        public void onDestroy() {


            Log.d(TAG, "Service is being destroyed");

            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Log.d(TAG, "MediaPlayer released");
                } catch (Exception e) {
                    Log.e(TAG, "Error releasing MediaPlayer: " + e.getMessage());
                } finally {
                    mediaPlayer = null;
                }
            }

            if (mSession != null) {
                mSession.setActive(false);
                mSession.release();

                mSession = null;
            }

            stopForeground(true);
            stopSelf();
            super.onDestroy();
        }
        @Override
        public void onTaskRemoved(Intent rootIntent) {

            super.onTaskRemoved(rootIntent);
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
                        if (mSession != null) {
                mSession.setActive(false);
                mSession.release();
                mSession = null;
            }

            stopForeground(true);
            stopSelf();
            stopSelf();
        }

        @Override
        public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                     int clientUid,
                                     Bundle rootHints) {

            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        }

        @Override
        public void onLoadChildren(@NonNull final String parentMediaId,
                                   @NonNull final Result<List<MediaItem>> result) {


            List<MediaItem> mediaItems = new ArrayList<>();
            for (Songmodel song : allSongs) {
                mediaItems.add(createMediaItem(song));
            }


            result.sendResult(mediaItems);
        }

        private MediaItem createMediaItem(Songmodel song) {
            MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                    .setMediaId(String.valueOf(song.getSong_id())) // Use song ID as media ID
                    .setTitle(song.getSong_name()) // Title of the song
                    .setSubtitle(song.getArtist()) // Artist of the song
                    .setMediaUri(Uri.parse(song.getPath()))// URI of the audio file
                    .build();
            return new MediaItem(description, MediaItem.FLAG_PLAYABLE);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                switch (action) {
                    case ACTION_PLAY:
                        mSession.getController().getTransportControls().play();
                        break;
                    case ACTION_PAUSE:
                        mSession.getController().getTransportControls().pause();
                        break;
                    case ACTION_NEXT:
                        mSession.getController().getTransportControls().skipToNext();
                        break;
                    case ACTION_PREVIOUS:
                        mSession.getController().getTransportControls().skipToPrevious();
                        break;
                    case ACTION_STOP:
                        mSession.getController().getTransportControls().stop();
                        break;
                    case ACTION_FAST_FORWARD:
                        mSession.getController().getTransportControls().fastForward();
                        break;
                    case ACTION_REWIND:
                        mSession.getController().getTransportControls().rewind();
                    default:
                        Log.w(TAG, "Unknown action: " + action);
                }
            }
            return START_NOT_STICKY;
        }
        public void updateseeeeek(){
            mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                            mediaPlayer.getCurrentPosition(), 1.0f)
                    .build());
        }



        private final class MediaSessionCallback extends MediaSessionCompat.Callback {
            private void play(int index) {

                if (allSongs != null && index >= 0 && index < allSongs.size() && mediaPlayer!=null) {
                    try {
                        currentSongIndex = index;

                        mediaPlayer.reset();
                        getsong = allSongs.get(index);
                        mediaPlayer.setDataSource(allSongs.get(index).getPath());
                        mediaPlayer.prepare(); // Prepare the MediaPlayer
                        mediaPlayer.start();
                        startUpdatingSeekBar();
                        autonextsong();
                        setmetadataa(getsong);

                        mSession.setPlaybackState(new PlaybackStateCompat.Builder()

                                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1.0f)
                                .build());
                        showNotification(PlaybackStateCompat.STATE_PLAYING, getsong.getSong_name());



                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            private void setmetadataa(Songmodel song) {
                Log.d(TAG, "setmetadataa: " + song.getSong_name());

                metadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(song.getSong_id()))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getSong_name())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.getPath())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbums())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration())
                        .build();
                Log.d(TAG, "setmetadataa: " + metadata);
                mSession.setMetadata(metadata);

            }

            @Override
            public void onPlay() {
                if (currentSongIndex == -1) {

                    play(0);
                    Log.d(TAG, "onPlay: ");
                } else {
                    Log.d(TAG, "onPlay: ");

                    mediaPlayer.start();
                    startUpdatingSeekBar();
                    mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1.0f)
                            .build());
                    showNotification(PlaybackStateCompat.STATE_PLAYING, getsong.getSong_name());

                }
            }

            private void autonextsong() {
                mediaPlayer.setOnCompletionListener(mp -> {

                    skipToNext();
                });
            }


            private void pause() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 1.0f)
                            .build());
                    showNotification(PlaybackStateCompat.STATE_PAUSED, getsong.getSong_name());
                }

            }

            private void skipToNext() {
                if (allSongs != null && currentSongIndex < allSongs.size() - 1) {
                    play(currentSongIndex + 1); // Play the next song
                }
            }

            private void skipToback() {
                if (allSongs != null && currentSongIndex < allSongs.size()) {
                    play(currentSongIndex - 1); // Play the next song
                }
            }

            private void stopSong() {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                            .build());
                    showNotification(PlaybackStateCompat.STATE_PAUSED, getsong.getSong_name());
                }
            }


            @Override
            public void onSkipToQueueItem(long queueId) {
            }

            @Override
            public void onSeekTo(long position) {
                if (mediaPlayer != null&&currentSongIndex!=-1) {
                    mediaPlayer.seekTo((int) position);


                }
            }

            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {

                if (extras != null) {
                    String extraValue = extras.getString("category");

                    Log.d(TAG, "Received extra value: " + extraValue);
               switch (extraValue){

                   case "ALLSONGS":
                       allSongs=songRepo.getAllSongs();
                       break;
                   case "FAVORATES":

                       allSongs=songRepo.getlikedsongs().getValue();
                       break;

                   case "ARTIST":
                       allSongs=songRepo.getArtistsongs();
                       break;
                   case "ALBUM":
                       allSongs=songRepo.getAlbumsongs();
                       break;


               }


                }
                updateMediaSessionQueue();



                for (int i = 0; i < allSongs.size(); i++) {

                    if (String.valueOf(allSongs.get(i).getSong_id()).equals(mediaId)) {
                        Log.d(TAG, "song finded on metadata");
                        play(i);
                        break;
                    }

                }
                if (extras != null) {
                    String extraValue = extras.getString("extra_key");
                    Log.d(TAG, "Received extra value: " + extraValue);
                }
            }
            private void updateMediaSessionQueue() {
                List<MediaSessionCompat.QueueItem> queueItems = new ArrayList<>();

                for (Songmodel song : allSongs) {
                    MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                            .setMediaId(String.valueOf(song.getSong_id()))
                            .setTitle(song.getSong_name())
                            .setSubtitle(song.getArtist())
                            .setMediaUri(Uri.parse(song.getPath()))
                            .build();

                    queueItems.add(new MediaSessionCompat.QueueItem(description, song.getSong_id()));
                }
            }
            @Override
            public void onRewind(){
                if (mediaPlayer != null) {
                    int newPosition = Math.max(mediaPlayer.getCurrentPosition() - 5000, 0); // Seek back 5 seconds
                    mediaPlayer.seekTo(newPosition);
                    Log.d(TAG, "Rewind to: " + newPosition);
                    updateseeeeek();


                }

            }
            @Override
            public void onFastForward(){
                if (mediaPlayer != null) {
                    int newPosition = Math.min(mediaPlayer.getCurrentPosition() + 5000, mediaPlayer.getDuration()); // Seek back 5 seconds
                    mediaPlayer.seekTo(newPosition);

                    updateseeeeek();

                }

            }

            @Override
            public void onPause() {

                pause();
                stopUpdatingSeekBar();

            }

            @Override
            public void onStop() {
                stopSong();
            }

            @Override
            public void onSkipToNext() {
                skipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                skipToback();
            }

            @Override
            public void onCustomAction(String action, Bundle extras) {
            }

            @Override
            public void onPlayFromSearch(final String query, final Bundle extras) {
            }

        }
    }