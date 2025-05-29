import controller.RealLabController;

public class RealLab {
    public static void main(String[] args) {
        RealLabController controller = new RealLabController();

        System.out.println("🔬 검사기관 - 전자봉투 생성 시작");

        // 사용자 입력 없이 고정 경로 사용
        String result = "data/result.txt";
        String mark = "data/certified_mark.png";

        //controller.create(result, mark);
    }
}