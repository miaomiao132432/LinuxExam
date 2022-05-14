import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.*;
import ai.djl.repository.zoo.*;
import ai.djl.paddlepaddle.zoo.cv.objectdetection.PpWordDetectionTranslator;
import ai.djl.paddlepaddle.zoo.cv.wordrecognition.PpWordRecognitionTranslator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OCREngine {
    public HesuanInfo run(String path) throws Exception {
        Path imageFile = Paths.get(path);
        Image img = ImageFactory.getInstance().fromFile(imageFile);

        Criteria<Image, DetectedObjects> criteria1 = Criteria.builder()
                .optEngine("PaddlePaddle")
                .setTypes(Image.class, DetectedObjects.class)
                .optModelUrls("https://resources.djl.ai/test-models/paddleOCR/mobile/det_db.zip")
                .optTranslator(new PpWordDetectionTranslator(new ConcurrentHashMap<String, String>()))
                .build();

        ZooModel<Image, DetectedObjects> detectionModel = criteria1.loadModel();

        Predictor<Image, DetectedObjects> detector = detectionModel.newPredictor();

        DetectedObjects detectedObj = detector.predict(img);
        Image newImage = img.duplicate();
        newImage.drawBoundingBoxes(detectedObj);
        newImage.getWrappedImage();

        List<DetectedObjects.DetectedObject> boxes = detectedObj.items();

        Image sample = getSubImage(img, boxes.get(2).getBoundingBox());
        sample.getWrappedImage();

        Image sample1 = getSubImage(img, boxes.get(7).getBoundingBox());

        Criteria<Image, String> criteria3 = Criteria.builder()
                .optEngine("PaddlePaddle")
                .setTypes(Image.class, String.class)
                .optModelUrls("https://resources.djl.ai/test-models/paddleOCR/mobile/rec_crnn.zip")
                .optTranslator(new PpWordRecognitionTranslator())
                .build();
        ZooModel<Image, String> recognitionModel = criteria3.loadModel();
        Predictor<Image, String> recognizer = recognitionModel.newPredictor();

        HesuanInfo info = new HesuanInfo();
        info.name = recognizer.predict(sample).toString();
        info.result = recognizer.predict(sample1).toString();

        detector.close();
        detectionModel.close();

        recognizer.close();
        recognitionModel.close();

        return info;
    }

    Image getSubImage(Image img, BoundingBox box) {
        Rectangle rect = box.getBounds();
        double[] extended = extendRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        int width = img.getWidth();
        int height = img.getHeight();
        int[] recovered = {
                (int) (extended[0] * width),
                (int) (extended[1] * height),
                (int) (extended[2] * width),
                (int) (extended[3] * height)
        };
        return img.getSubImage(recovered[0], recovered[1], recovered[2], recovered[3]);
    }

    double[] extendRect(double xmin, double ymin, double width, double height) {
        double centerx = xmin + width / 2;
        double centery = ymin + height / 2;
        if (width > height) {
            width += height * 2.0;
            height *= 3.0;
        } else {
            height += width * 2.0;
            width *= 3.0;
        }
        double newX = centerx - width / 2 < 0 ? 0 : centerx - width / 2;
        double newY = centery - height / 2 < 0 ? 0 : centery - height / 2;
        double newWidth = newX + width > 1 ? 1 - newX : width;
        double newHeight = newY + height > 1 ? 1 - newY : height;
        return new double[] { newX, newY, newWidth, newHeight };
    }
}
