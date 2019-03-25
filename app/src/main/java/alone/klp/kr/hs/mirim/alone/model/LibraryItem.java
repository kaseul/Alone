package alone.klp.kr.hs.mirim.alone.model;

public class LibraryItem {
    public String title;
    public String content;
    public String length;
    public String url;
    public boolean ifFav = false;
    public boolean isPlay = false;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        LibraryItem other = (LibraryItem) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;

        return true;
    }
}