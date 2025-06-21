package com.ideaflow.noveldownload.novel.handle;

import com.ideaflow.noveldownload.novel.model.AppConfig;
import lombok.experimental.UtilityClass;


@UtilityClass
public class PostHandlerFactory {

    public PostProcessingHandler getHandler(String extName, AppConfig config) {
        return switch (extName) {
            case "txt" -> new TxtMergeHandler(config);
            case "epub" -> new EpubMergeHandler();
            case "html" -> new HtmlCatalogHandler();
            default -> throw new IllegalArgumentException("Unsupported format: " + extName);
        };
    }
}