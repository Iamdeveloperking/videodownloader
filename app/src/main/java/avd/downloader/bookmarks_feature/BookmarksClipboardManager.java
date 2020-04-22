

package avd.downloader.bookmarks_feature;

import android.database.Cursor;
import android.database.DatabaseUtils;

class BookmarksClipboardManager implements BookmarksSQLite.OnBookmarkPositionChangedListener {
    private ClipBoard clipBoard;
    private BookmarksSQLite sqLite;

    class ClipBoard {
        String table;
        int position;
        String type;
        byte[] icon;
        String title;
        String link;
        boolean move;
    }

    BookmarksClipboardManager(BookmarksSQLite sqLite) {
        this.sqLite = sqLite;
        this.sqLite.setOnBookmarkPositionChangedListener(this);
    }

    @Override
    public void onBookmarkPositionChanged(int oldPosition, int newPosition) {
        if (clipBoard != null && clipBoard.position == oldPosition) {
            if (newPosition > 0) {
                clipBoard.position = newPosition;
            } else {
                clipBoard = null;
            }
        }
    }

    private void storeToClipboard(int position) {
        Cursor c = sqLite.getBookmarksDatabase().query(sqLite.getCurrentTable(), null, "oid = " +
                position, null, null, null, null);
        c.moveToNext();
        clipBoard = new ClipBoard();
        clipBoard.table = sqLite.getCurrentTable();
        clipBoard.position = position;
        clipBoard.type = c.getString(c.getColumnIndex("type"));
        clipBoard.icon = c.getBlob(c.getColumnIndex("icon"));
        clipBoard.title = c.getString(c.getColumnIndex("title"));
        clipBoard.link = c.getString(c.getColumnIndex("link"));
        c.close();
    }

    void copy(int position) {
        storeToClipboard(position);
        clipBoard.move = false;
    }

    void cut(int position) {
        storeToClipboard(position);
        clipBoard.move = true;
    }

    boolean paste() {
        if (clipBoard.type.equals("link")) {
            return paste((int) (DatabaseUtils.queryNumEntries(sqLite.getBookmarksDatabase(), sqLite
                    .getCurrentTable())) + 1);
        } else {
            Cursor c = sqLite.getFolders();
            boolean pasted = paste(c.getCount() + 1);
            c.close();
            return pasted;
        }
    }

    boolean paste(int position) {
        if (clipBoard != null) {
            if (!clipBoard.move) {
                sqLite.insert(position, clipBoard.type, clipBoard.icon, clipBoard.title, clipBoard.link);
                if (clipBoard.type.equals("folder")) {
                    sqLite.copyFolderContents(clipBoard.table + "_" + clipBoard.position, sqLite
                            .getCurrentTable() + "_" + position);
                }
                return true;
            } else {
                sqLite.moveItem(clipBoard.table, clipBoard.position, position);
                clipBoard = null;
                return true;
            }
        } else {
            return false;
        }
    }

    boolean isClipboardEmpty() {
        return clipBoard == null;
    }

    ClipBoard getClipBoard() {
        return clipBoard;
    }
}
