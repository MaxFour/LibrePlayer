package com.maxfour.music.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastFmTrack {

    @Expose
    private Track track;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public static class Track {
        @SerializedName("name")
        @Expose
        private String name;
        @Expose
        private Album album;
        @Expose
        private Wiki wiki;
        @Expose
        private Toptags toptags;
        @Expose
        private Artist artist;

        public Album getAlbum() {
            return album;
        }

        public Wiki getWiki() {
            return wiki;
        }

        public String getName() {
            return name;
        }

        public Toptags getToptags() {
            return toptags;
        }

        public static class Artist {

            @Expose
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class Wiki {
            @Expose
            private String published;

            public String getPublished() {
                return published;
            }

            public void setPublished(String published) {
                this.published = published;
            }
        }

        public static class Toptags {
            @Expose
            private List<Tag> tag = null;


            public List<Tag> getTag() {
                return tag;
            }

            public static class Tag {
                @Expose
                private String name;

                public String getName() {
                    return name;
                }
            }
        }

        public static class Album {
            @Expose
            private String artist;
            @Expose
            private List<Image> image = null;
            @Expose
            private String title;
            @SerializedName("@attr")
            @Expose
            private Attr attr;

            public Attr getAttr() {
                return attr;
            }

            public void setAttr(Attr attr) {
                this.attr = attr;
            }

            public String getArtist() {
                return artist;
            }

            public void setArtist(String artist) {
                this.artist = artist;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<Image> getImage() {
                return image;
            }

            public void setImage(List<Image> image) {
                this.image = image;
            }

            public static class Attr {
                @Expose
                private String position;

                public String getPosition() {
                    return position;
                }

                public void setPosition(String position) {
                    this.position = position;
                }
            }

            public class Image {

                @SerializedName("#text")
                @Expose
                private String text;
                @Expose
                private String size;

                public String getSize() {
                    return size;
                }

                public String getText() {
                    return text;
                }
            }
        }
    }
}
