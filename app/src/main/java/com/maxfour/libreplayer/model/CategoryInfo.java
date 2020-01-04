package com.maxfour.libreplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.maxfour.libreplayer.R;


public class CategoryInfo implements Parcelable {

    public enum Category {
        HOME(R.id.action_home, R.string.home, R.drawable.ic_home_white_24dp),
        SONGS(R.id.action_song, R.string.songs, R.drawable.ic_audiotrack_white_24dp),
        ALBUMS(R.id.action_album, R.string.albums, R.drawable.ic_album_white_24dp),
        ARTISTS(R.id.action_artist, R.string.artists, R.drawable.ic_artist_white_24dp),
        PLAYLISTS(R.id.action_playlist, R.string.playlists, R.drawable.ic_playlist_play_white_24dp),
        GENRES(R.id.action_genre, R.string.genres, R.drawable.ic_guitar_white_24dp),
        QUEUE(R.id.action_playing_queue, R.string.queue, R.drawable.ic_queue_music_white_24dp);

        public final int icon;

        public final int id;

        public final int stringRes;

        Category(int id, int stringRes, int icon) {
            this.stringRes = stringRes;
            this.id = id;
            this.icon = icon;
        }
    }

    public static final Creator<CategoryInfo> CREATOR = new Creator<CategoryInfo>() {
        public CategoryInfo createFromParcel(Parcel source) {
            return new CategoryInfo(source);
        }

        public CategoryInfo[] newArray(int size) {
            return new CategoryInfo[size];
        }
    };

    public Category category;

    public boolean visible;

    public CategoryInfo(Category category, boolean visible) {
        this.category = category;
        this.visible = visible;
    }

    private CategoryInfo(Parcel source) {
        category = (Category) source.readSerializable();
        visible = source.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(category);
        dest.writeInt(visible ? 1 : 0);
    }
}
