package com.ideaflow.noveldownload.novel.util;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;


/**
 * 文件工具类
 * <p>
 * 提供文件排序、获取远程文件大小、替换文件名非法字符等功能。
 */
@UtilityClass
public class FileUtils {

    // 文件排序，按文件名升序
    public List<File> sortFilesByName(File dir) {
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.isFile() && file.getName().startsWith(".") != true) // 排除文件夹和隐藏文件(例如：.DS_Store)
                .sorted((o1, o2) -> {
                    // 如果文件名包含下划线，则按下划线前的数字排序
                    try {
                        int no1 = Integer.parseInt(StrUtil.subBefore(o1.getName(), "_", false));
                        int no2 = Integer.parseInt(StrUtil.subBefore(o2.getName(), "_", false));
                        return no1 - no2;
                    } catch (NumberFormatException e) {
                        // 如果转换失败，则按文件名字符串排序
                        return o1.getName().compareTo(o2.getName());
                    }
                }).toList();
    }

    /**
     * 获取远程文件大小，HEAD 请求不会下载文件内容，只会返回文件的元数据（例如文件大小）
     *
     * @return 字节
     */
    @SneakyThrows
    public long fileSize(String fileUrl) {
        URL url = new URI(fileUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 使用 HEAD 请求方法，只获取头部信息
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);
        int contentLength = conn.getContentLength();
        conn.disconnect();

        return contentLength == -1 ? 0 : contentLength;
    }

    /**
     * 替换文件名非法字符，仅用于文件名而非路径
     */
    public String sanitizeFileName(String fileName) {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return fileName
                    .replace(':', '：')
                    .replace('*', '＊')
                    .replace('?', '？')
                    .replace('"', '\'')
                    .replace('<', '＜')
                    .replace('>', '＞')
                    .replaceAll("[/\\\\|]", "_");
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            return fileName
                    .replace('.', '。')
                    .replace(':', '：')
                    .replace('/', '／')
                    .replace('\000', '_');
        } else { // others
            return fileName.replace("/", "");
        }

    }

    /**
     * 解析路径字符串 s，如果是绝对路径，则直接返回；
     * 如果是相对路径，则基于当前工作目录拼接并返回完整路径。
     *
     * @param s 路径字符串，可以是相对路径或绝对路径
     * @return 解析后的路径字符串
     */
    public String resolvePath(String s) {
        String basePath = System.getProperty("user.dir");
        Path path = Paths.get(s);
        return path.isAbsolute()
                ? path.toString()
                : Paths.get(basePath, s).toString();
    }

}
