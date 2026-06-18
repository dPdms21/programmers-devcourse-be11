package part3;

// * I/O Stream (입출력 스트림)
// 자바에서 데이터를 입력받거나 출력할 떄 사용하는, 데이터가 흐르는 통로(Stream)
// 스트림은 단방향이라 입력용(InputStream)과 출력용(OutputStream)이 따로 존재

// * 바이트 스트림 vs 문자 스트림
// - 바이트 스트림: 1바이트 단위로 데이터를 처리
// InputStream, OutputStream
// - 문자 스트림: 2바이트(문자) 단위로 데이터를 처리 -> 텍스트, 문자 스트림이 적합
// Writer, Reader
// -> 스트림 사용 후 반드시 close로 자원을 해제해야함 -> (try-with-resources)

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class D_input_output_stream {
    private String dPath;
    private String fPath;
    // * java.nio.file 패키지 (NIO)
    // - Path: 파일이나 폴더의 경로를 객체로 표현
    // - Files: 파일/폴더의 생성, 존재 여부 확인, 삭제 등 유틸리티 메서드를 제공
    private Path myFolder;
    private String today;
    private Path todayFile;

    public D_input_output_stream() {
        // System.getProperty("user.home")은 현재 사용자의 홈 디렉터리 경로를 문자열로 반환
        // - JVM이 실행 환경에서 읽어온 "시스템 프로퍼티" 중 하나
        // - Windows면 C:\Users\사용자이름, macOS면 /Users/사용자이름, Linux면 /home/사용자이름 같은 값
        // - 즉 OS나 계정이 달라도 각자의 홈 경로를 알아서 가져옴
        // 여기에 File.separator(OS별 경로 구분자 / 또는 \)와 "Desktop"을 붙여 바탕화면 경로를 만듦
        this.dPath = "D:" + File.separator + "dev";
        this.fPath = this.dPath + File.separator + "Sample";

        // Paths.get(...)은 문자열로 된 경로를 Path 객체로 변환
        // - String은 단순한 글자 덩어리라서 파일/폴더 작업에 바로 쓸 수 없음
        // - Path 객체로 바꾸면 Files 클래스의 메서드(생성, 존재 확인, 삭제 등)에 넘길 수 있음
        // - 또 getFileName(), getParent() 같은 경로 전용 기능도 쓸 수 있음
        // 참고: 자바 11 이상에서는 Path.of(folderPath)로 써도 동일
        this.myFolder = Path.of(this.fPath);
    }

    // 1. 폴더 생성
    // - Files.notExists(Path) : 해당 경로가 존재하지 않으면 true를 반환
    // - Files.createDirectory(Path) : 경로에 해당하는 폴더(디렉터리)를 생성
    public void exam1() {
        try {
            if (Files.notExists(myFolder)) {
                Files.createDirectories(myFolder);
            }
            else {
                System.out.println("폴더 이미 존재");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 2. 파일에 내용 쓰기
    //  •  FileOutputStream으로 파일에 바이트 데이터를 기록
//  •  바이트 스트림은 1바이트 단위로 처리하므로, 문자열은 getBytes()로 byte 배열로 변환한 뒤 write()로 씀
    public void exam2() {
        // LocalDate.now(): 오늘 날짜를 가져오고, 지정한 패턴("yyyy-MM-dd")의 문자열로 포맷
        today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Path.resolve(...) : 기준 경로(myFolder) 뒤에 하위 경로를 이어 붙여 새 Path 생성
        todayFile = myFolder.resolve(today + ".txt");


        // 방법.1 : 전통적인 방식 (직접 close)
        // finally 블록에서 스트림을 직접 닫아주어야 하므로 코드가 길고 복잡
            /*
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(todayFile.toFile());
                String content = "Hello World!";
                fos.write(content.getBytes());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            */

        // 방법.2 -> try-with-resources
        // try(...) 괄호 안에서 선언한 자원은 try 블록이 끝나면 자동으로 close()가 호출
        // 따라서 finally로 직접 닫을 필요가 없어 코드가 간결하고 안전
        if (Files.notExists(todayFile)) {
            try (FileOutputStream fos = new FileOutputStream(todayFile.toFile())) {
                String content = "HelloWorld";
                fos.write(content.getBytes());
                System.out.println(today + ".txt 파일을 생성하고 내용 쓰기");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 2-1. QR코드 이미지 만들기 (바이트 스트림 활용)
    // - QR코드는 이미지(PNG)이므로 문자가 아닌 "바이너리 데이터" → 바이트 스트림이 적합
    // - exam2()처럼 텍스트가 아니라 이미지라서, 문자 스트림으로는 다룰 수 없는 대표 예시
    // - ZXing 라이브러리로 문자열을 QR코드 행렬(BitMatrix)로 만든 뒤 PNG 파일로 저장
    public void exam2_1() {
        today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        todayFile = myFolder.resolve(today + ".png");

        // QR코드에 담을 내용 (UR:, 텍스트 등 무엇이든 가능)
        String content = "https://programmers.co.kr";

        if (Files.notExists(todayFile)) {
            try {
                // 1) QRCodeWriter.encode(...) : 문자열을 QR코드 행렬(BitMatrix)로 변환
                // - 인자: 내용, 포맷(QR_CODE), 가로 크기, 세로 크기(픽셀)
                QRCodeWriter w = new QRCodeWriter();
                BitMatrix matrix = w.encode(content, BarcodeFormat.QR_CODE, 300, 300);

                // 2) MatrixToImageWriter.writeToPath(...) : 행렬을 실제 PNG 이미지 파일로 저장
                // - 내부적으로 ImageIO(바이트 스트림)를 이용해 바이너리로 기록
                MatrixToImageWriter.writeToPath(matrix, "PNG", todayFile);

                System.out.println(today + ".png QR코드 이미지 생성");
            }
            catch (WriterException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println(today + ".png 파일이 이미 존재");
        }
    }

    // 2-2. 파일에 내용 쓰기 - 문자
    // - exam2()는 바이트 스트림(FileOutputStream)이라 문자열을 getBytes()로 byte 배열로 바꿔서 써야 했음
    // - 문자 스트림(FileWriter)은 문자(char) 단위로 처리하므로 String을 그대로 write()에 넘길 수 있음 (byte 변환 불필요)
    // - 그래서 텍스트 파일을 다룰 때는 문자 스트림이 더 간편
    public void exam2_2() {
        today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        todayFile = myFolder.resolve(today + "_writer.txt");

        if (Files.notExists(todayFile)) {
            try (FileWriter fw = new FileWriter(todayFile.toFile())) {
                String content = "Hello World!";
                fw.write(content);
                System.out.println("파일을 생성하고 내용 씀");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("이미 존재");
        }
    }

    // 3. 파일 내용 읽기 <-> exam2()의 짝
    // - FileInputStream으로 파일을 1바이트 단위로 읽어 들임
    // - 읽은 byte 배열을 new String(...)으로 다시 문자열로 변환해야 사람이 읽을 수 있음
    public void exam3() {
        today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        todayFile = myFolder.resolve(today + ".txt");

        if ( Files.exists(todayFile) ) {

            try ( FileInputStream fis = new FileInputStream(todayFile.toFile()) ) {

                byte[] bytes = fis.readAllBytes();
                String content = new String(bytes);
                System.out.println("내용 : "  + content);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("파일이 존재하지 않음");
        }

    }

    // 3-1. QR코드 이미지 읽기
    public void exam3_1() {
        today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        todayFile = myFolder.resolve(today + ".png");

        if ( Files.exists(todayFile) ) {

            try {
                // 1) ImageIO.read(...) : PNG 파일을 BufferedImage(이미지 객체)로 읽어옴
                BufferedImage image = ImageIO.read(todayFile.toFile());

                // 2) 이미지를 ZXing이 해석할 수 있는 형태(BinaryBitmap)로 변환
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                // 3) MultiFormatReader.decode(...) : 비트맵에서 QR코드 내용을 추출
                Result result = new MultiFormatReader().decode(bitmap);
                System.out.println("내용 : " + result.getText());

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("파일이 존재하지 않음");
        }
    }

    // 3-2. 파일 내용 읽기 문자 스트림
    //	•	FileReader로 파일을 문자(char) 단위로 읽음
    //	•	바이트 스트림과 달리 byte → String 변환이 필요 없음
    //	•	보통 BufferedReader로 감싸 한 줄씩(readLine) 편하게 읽음
    public void exam3_2() {
        today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        todayFile = myFolder.resolve(today + "_writer.txt");

        if ( Files.exists(todayFile) ) {

            // FileReader(문자 스트림)를 BufferedReader로 감싸 줄 단위로 읽음
            try ( BufferedReader br = new BufferedReader(new FileReader(todayFile.toFile())) ) {
                String line;
                StringBuilder sb = new StringBuilder();

                while ( (line = br.readLine()) != null ) {
                    sb.append(line).append("\n");
                }

                System.out.println("내용 : " + sb);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("파일이 존재하지 않음");
        }
    }

    public static void main(String[] args) {
        D_input_output_stream d = new D_input_output_stream();

        d.exam1();
    }
}
