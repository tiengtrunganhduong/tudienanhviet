package com.k43nqtn.tudienanhviet;

import android.provider.BaseColumns;

final class DictDbContract {
    static final String DATABASE_NAME = "dictionary.db";
    static final int DATABASE_VERSION = 1;

    static final String COLUMN_ID = BaseColumns._ID;
    static final String COLUMN_WORD = "word";
    static final String COLUMN_DETAILS = "details";

    static final String TABLE_EV_1 = "en_vi";
//    static final String TABLE_EV_2 = "en_vi_lingoes";
    static final String TABLE_E_1 = "wordnet";
    static final String TABLE_VE_1 = "vi_en_hnduc";
    static final String TABLE_VE_2 = "vi_en";
    static final String TABLE_VE_3 = "vi_en_vnedict";

    static final String TITLE_EV_1 = "Anh Việt - Hồ Ngọc Đức";
//    static final String TITLE_EV_2 = "Anh Việt - Lingoes";
    static final String TITLE_E_1 = "Anh Anh - WordNet";
    static final String TITLE_VE_1 = "Việt Anh - Hồ Ngọc Đức";
    static final String TITLE_VE_2 = "Việt Anh";
    static final String TITLE_VE_3 = "Việt Anh - VNEDICT";

    static class EnPronTable implements BaseColumns {
        static final String TABLE_NAME = "en_pron_map";
        static final String COLUMN_NAME_WORD = "word";
        static final String COLUMN_NAME_ENCODED = "encoded";
        static final String COLUMN_NAME_PATHS = "paths";
    }
}
