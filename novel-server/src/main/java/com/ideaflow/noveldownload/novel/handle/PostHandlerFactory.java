package com.ideaflow.noveldownload.novel.handle;

import com.ideaflow.noveldownload.constans.CommonConst;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import lombok.experimental.UtilityClass;


@UtilityClass
public class PostHandlerFactory {

    public PostProcessingHandler getHandler(String extName, AppConfig config) {
        return switch (extName) {
            case CommonConst.SAVE_TYPE_TEXT -> new TxtMergeHandler(config);
            case CommonConst.SAVE_TYPE_EPUB -> new EpubMergeHandler();
            case CommonConst.SAVE_TYPE_HTML -> new HtmlTocHandler();
            case CommonConst.SAVE_TYPE_PDF -> new PdfMergeHandler(config);
            default -> throw new IllegalArgumentException("Unsupported format: " + extName);
        };
    }
}