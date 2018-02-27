package com.qinmei.databasebackup.com.qinmei.controller;

import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 控制器
 */
@RestController
public class DatabaseController extends TimerTask {

     /** 数据库备份
	 * <p>
	 * Title: exportDatabaseTool
	 * </p>
            * <p>
	 * Description:
            * </p>
            *
            * @param hostIP
	 * @param userName
	 * @param password
	 * @param savePath
	 * @param fileName
	 * @param databaseName
	 * @return
             * @throws InterruptedException
	 */
    public static boolean exportDatabaseTool(String hostIP, String userName, String password, String savePath,
                                             String fileName, String databaseName) throws InterruptedException {
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {// 如果目录不存在
            saveFile.mkdirs();// 创建文件夹
        }
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }

        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(savePath + fileName), "utf8"));
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("mysqldump").append(" --opt").append(" -h").append(hostIP);
            stringBuilder.append(" --user=").append(userName).append(" --password=").append(password)
                    .append(" --lock-all-tables=true");
            stringBuilder.append(" --result-file=").append(savePath + fileName).append(" --default-character-set=utf8 ")
                    .append(databaseName);

            Process process = Runtime.getRuntime().exec(stringBuilder.toString());

            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                printWriter.println(line);
            }
            printWriter.flush();

            if (process.waitFor() == 0) {// 0 表示线程正常终止。
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 数据库恢复
     * <p>
     * Title: recover
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param path
     * @throws IOException
     */
    public static void recover(String path) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        // 恢复 到数据库的账户信息
        Process process = runtime.exec("mysql -h 192.168.25.129 -u root -p123456 --default-character-set=utf8 my");
        OutputStream outputStream = process.getOutputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String str = null;
        StringBuffer sb = new StringBuffer();
        while ((str = br.readLine()) != null) {
            sb.append(str + "\r\n");
        }
        str = sb.toString();
        System.out.println(str);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "utf-8");
        writer.write(str);
        writer.flush();
        outputStream.close();
        br.close();
        writer.close();
    }

    /**
     * 定时方法
     * <p>Title: getEmpById</p>
     * <p>Description: </p>
     * @throws IOException
     */
    public void run(){
        Timer timer = new Timer(true);
        timer.schedule(

                new java.util.TimerTask() {
                    public void run(){
                        try {
                            databaseBackup();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, 0, 1 * 60 * 1000);
    }

    /**
     * 数据库备份恢复操作
     * <p>Title: checkNewMail</p>
     * <p>Description: </p>
     * @throws IOException
     */
    public void databaseBackup() throws IOException {
        try {
            // 备份
            exportDatabaseTool("127.0.0.1", "root", "root", "D:/backupDatabase", "qinmei.sql", "qinmei");
            // 恢复
            recover("D:/backupDatabase/qinmei.sql");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
