package org.example;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static {
        NativeLoader nativeLoader = new NativeLoader();
        nativeLoader.loadLibrary("opencv_java-480");
    }

    public static void main(String[] args) {
        int[] targetSize = {78, 78};
        String inputDirectory = "src/main/resources/img"; // 디렉토리에 여러 이미지 파일이 있어야 합니다
        String outputDirectory = "src/main/resources/convert"; // 처리된 이미지를 저장할 디렉토리

        File inputDir = new File(inputDirectory);
        File[] imageFiles = inputDir.listFiles(); // .jpg 확장자를 가진 이미지 파일만 선택
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (File imageFile : imageFiles) {
            executorService.execute(() -> {
                Mat frame = Imgcodecs.imread(imageFile.getAbsolutePath());

                if (frame.empty()) {
                    System.out.println("Image not found: " + imageFile.getName());
                    return;
                }

                double widthRatio = (double) targetSize[0] / frame.width();
                double heightRatio = (double) targetSize[1] / frame.height();
                double scale = Math.min(widthRatio, heightRatio);

                Mat processedFrame = new Mat();
                Imgproc.resize(frame, processedFrame, new org.opencv.core.Size(), scale, scale);

                // Save the processed image with the same file name in the output directory
                String outputFileName = outputDirectory + File.separator + imageFile.getName();
                Imgcodecs.imwrite(outputFileName, processedFrame);
                String HashKey = ImgHashKey.getImgHashKey(processedFrame);
                System.out.println("Processed: " + imageFile.getName() + "\t HashKey = " + HashKey);
            });

        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
