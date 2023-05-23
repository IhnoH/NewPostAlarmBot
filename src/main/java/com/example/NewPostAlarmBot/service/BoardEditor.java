package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.BoardDto;
import com.example.NewPostAlarmBot.DTO.PostDto;
import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jvnet.hk2.annotations.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Transactional
public class BoardEditor {
    private final BoardService boardService;

    public Document doc;
    public String tbody;

    public static Map<String, PostDto> postMap = new ConcurrentHashMap<>();
    public static Map<String, BoardDto> boardList = new ConcurrentHashMap<>();

    public BoardEditor(PostService postService, BoardService boardService) {
        this.boardService = boardService;
    }

    public void init(DomainInfoDto info){
        String url = info.getUrl();
        if(!postMap.containsKey(url)){
            PostDto postDto = new PostDto();
            postDto.setUrl(url);
            postDto.setTitleClass("");
            postDto.setNumClass("");
            postDto.setUrlTitle(doc.title());
            if(info.getKeyword() != null) postDto.setKeyword(info.getKeyword());
            postMap.put(url, postDto);
//            System.out.println("BoardRepo size: " + boardService.findAll().size());
//            for(BoardDto b: boardService.findAll()) System.out.println(b.url);
        }
    }

    public List<String> boardUpdate(String url){
        List<String> newTitle = new ArrayList<>();
        List<String> curTitle = findPostList(url);
        PostDto postDto = postMap.get(url);
        String topTitle = postDto.getTopTitle();
        List<String> titListKeyword = new ArrayList<>();
        String keyword = postMap.get(url).getKeyword();

        //System.out.println(topTitle);

        //for(String t: curTitle) System.out.println(t);
        //System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");

        if(postDto.getTopTitle() != null){
            for (String cur : curTitle) {
                if (!topTitle.equals(cur)) {
                    newTitle.add(cur);
//                    System.out.println(cur);
                } else break;
            }
            if (newTitle.size() > 0) {
                postDto.setTopTitle(newTitle.get(0));
                if(postMap.get(url).getBoardSize() == 0) {
                    postMap.get(url).setBoardSize(curTitle.size());
                    if(curTitle.size() == newTitle.size()) newTitle.clear();
                }
                else if(postMap.get(url).getBoardSize() == newTitle.size())
                    newTitle.clear(); // 새로운 제목 리스트여도 개수가 전체 게시글 개수와 같으면 보내지 않음
            }
        }
        else postDto.setTopTitle(curTitle.get(0));
        postMap.put(url, postDto);

        // 키워드가 존재할때 키워드를 포함한 제목 리스트 보냄
        if(keyword != null){
            for (String tit: newTitle){
                if (tit.contains(keyword)) titListKeyword.add(tit);
            }
            return titListKeyword;
        }

//        System.out.println("postMap " + postMap.size());
//        System.out.println(postMap.toString());
//        System.out.println("BoardList " + boardList.size());
//        System.out.println(boardList.toString());
//        System.out.println("\n");

        return newTitle;
    }

    public List<String> findPostList(String url){
        List<String> titleList = new ArrayList<>();
        List<String> numList = new ArrayList<>();
        List<String> titListWithoutNotice = new ArrayList<>();

        String src = doc.outerHtml();
        tbody = getTbody(src);

        BoardDto board;

        if(boardList.containsKey(url)) {
            if (boardList.get(url).title.equals("")) findPostClass(url);
            board = boardList.get(url);
        }
        else{
            findPostClass(url);
            board = boardList.get(url);
        }
        try {
            titleList = doc.getElementsByClass(board.title).stream().map(Element::text).collect(Collectors.toList());
            numList = doc.getElementsByClass(board.num).stream().map(Element::text).collect(Collectors.toList());
        }catch (Exception ignored){}

        if (titleList.size() == 0) titleList = getElem(board.title);
        if (numList.size() == 0) numList = getElem(board.num);

        // 게시글 상단의 공지로 고정되어 있는 게시글 제거
        if(titleList.size() == numList.size()){
            for(int i = 0;i<titleList.size();i++)
                if(isDigit(numList.get(i)))
                    titListWithoutNotice.add(titleList.get(i));
        }
        else{
            for(int i = 0;i<titleList.size();i++)
                if(i >= titleList.size() - numList.size())
                    titListWithoutNotice.add(titleList.get(i));
        }

//        System.out.println(url);
//        System.out.println("topTitle: " + titListWithoutNotice.get(0));
//        System.out.println("titleList size: "+titleList.size());
//        System.out.println("titListWithoutNotice size: " + titListWithoutNotice.size());
//        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ\n");
//        for(String t:titListWithoutNotice) System.out.println(t);
//        System.out.println(numList + " " + numList.size());

        return titListWithoutNotice;
    }

    public void findPostClass(String url){
        List<String> classes = new ArrayList<>(Arrays.asList(tbody.split("class=\"")));
        classes.forEach(t -> classes.set(classes.indexOf(t), t.split("\"")[0]));
        classes.remove(0);

        List<String> boardClassList = findClassListFreq(classes);
        boardClassList.addAll(findClassListPattern(classes));
        boardClassList = boardClassList.stream().distinct().collect(Collectors.toList());

        //System.out.println(boardClassList);

        String titClass = getTitleClass(boardClassList);
        String numClass = getNumClass(boardClassList);

        List<String> titleList = getElem(titClass);
        //System.out.println(titClass + " " + numClass);
        //for(String b: boardClassList) System.out.println(b + " " + getElem(b));
        //System.out.println(titleList);

        postMap.get(url).setTitleClass(titClass);
        postMap.get(url).setNumClass(numClass);

        BoardDto dto = new BoardDto(url, titClass, numClass);
        boardList.put(url, dto);
        boardService.save(dto);
    }


    public Document getDoc(String url) throws IOException {
        doc = Jsoup.connect(url)
                .header("content-type", "application/json;charset=UTF-8")
                .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .get();
        return doc;
    }

    public String getTbody(String src){
        String tbody = "";
        if(src.contains("<tbody") && src.contains("</tbody>")) {
            List<Integer> tbodyStart = new ArrayList<>();
            List<Integer> tbodyEnd = new ArrayList<>();
            List<Integer> tbodyIdx = new ArrayList<>();

            Matcher matcher = Pattern.compile("<tbody").matcher(src);
            while (matcher.find()) tbodyStart.add(matcher.start());

            matcher = Pattern.compile("</tbody>").matcher(src);
            while (matcher.find()) tbodyEnd.add(matcher.start());

            tbodyIdx.addAll(tbodyStart);
            tbodyIdx.addAll(tbodyEnd);
            Collections.sort(tbodyIdx);
            //System.out.println("tbody List: " + tbodyIdx);

            Stack<Integer> stack = new Stack<>();
            for(Integer i: tbodyIdx){
                if(tbodyEnd.contains(i)){
                    Integer start = stack.pop();
                    String tmp = src.substring(start + 8, i);
                    if(tmp.length() > tbody.length()) tbody = tmp;
                }
                stack.push(i);
            }
        }
        else if(src.contains("<body") && src.contains("</body>"))
            tbody = src.substring(src.indexOf("<body"), src.indexOf("</body>")+("</body>").length());
        else tbody = null;

        return tbody;
    }
    public List<String> getElem(String clss){
        List<String> elemList = new ArrayList<>();
        String pattern = "<[^>]*class=\"[^\"]*" + clss + "[^>]*>";
        List<String> tmp = new ArrayList<>(Arrays.asList(tbody.replaceAll("\n", "").replaceAll("\t", "").split(pattern)));
        tmp.remove(0);
        //System.out.println(tmp.size());
        for(String t: tmp){
            //System.out.println(t + "\n\n");
            while(t.indexOf(" ") == 0) {
                t = t.replaceFirst(" ", "");
                //System.out.println("t:" + t);
            }

            while(t.indexOf("<") == 0) t = t.replace(t.substring(0, t.indexOf(">")+1), "");
            if(t.replaceAll(" ", "").length() == 0) continue;
            String elemTmp = t.replace(t.substring(t.indexOf("<"), t.length()), "");
            while(elemTmp.indexOf(" ") == 0) elemTmp = elemTmp.replaceFirst(" ", "");
            if(elemTmp.replaceAll(" ", "").length() == 0) continue;
            elemList.add(elemTmp);
        }
        return elemList;
    }

    public List<String> findClassListFreq(List<String> classes) {
        HashSet<String> tmpSet = new HashSet<>(classes);
        List<String> nameSet = new ArrayList<>(tmpSet);

        List<Integer> classFreq = new ArrayList<>(); // 중복 제거 클래스 이름 빈도
        List<String> classNameList = new ArrayList<>(); // 중복 제거 클래스 이름 목록

        for (String name : nameSet) {
            int freq = Collections.frequency(classes, name);
            if (freq > 6 && !Objects.equals(name, " ") && !name.contains("{") && !Objects.equals(name, "")) {
                //System.out.println(freq + " " + name);
                classNameList.add(name);
                classFreq.add(freq);
                //System.out.println(name + " : " + freq);
            }
        }
        //for(int i = 0;i<classNameList.size();i++) System.out.println("class elem: " + classFreq.get(i) + " " + classNameList.get(i));
        //System.out.println("classNameList size: " + classNameList.size() + ", classFreq size: " + classFreq.size());

        HashSet<Integer> tmpFreq = new HashSet<>(classFreq);
        Integer[] freqSet = tmpFreq.toArray(new Integer[0]);    // 클래스 이름 빈도의 중복 제거

        List<Integer> freqOfFreq = new ArrayList<Integer>();    // 클래스 이름 빈도의 빈도

        for (Integer i : freqSet) freqOfFreq.add(Collections.frequency(classFreq, i));

//        System.out.println(classNameList);
//        System.out.println(classFreq);
//        System.out.println(freqOfFreq + ", " + Arrays.toString(freqSet));

        int maxFreq = freqSet[freqOfFreq.indexOf(Collections.max(freqOfFreq))]; // 클래스 이름 빈도의 빈도의 최대값

        List<String> boardClassList = new ArrayList<>(); // 게시글 정보 클래스 이름 리스트

        for (int i = 0; i < classFreq.size(); i++)
            if (classFreq.get(i) == maxFreq) boardClassList.add(classNameList.get(i));

        return boardClassList;
    }
    public List<String> findClassListPattern(List<String> classes){
        String strClass = String.join(",", classes);
        List<String> tmp = new ArrayList<>();
        List<String> invalid = new ArrayList<>();
        List<String> master = new ArrayList<>();
        String joker = "[^,]*";

        int invalidNum = 7;
        int preIF = 0;

        for (int i = 0; i < classes.size(); i++) {
            String clss = classes.get(i);
            if(tmp.contains(clss)) {
                master.addAll(tmp);
                tmp.clear();
                continue;
            }
            if(invalid.contains(clss)) continue;
            else tmp.add(clss);

            int strFreq = countStr(strClass, String.join(",", tmp));
            int idFreq = Collections.frequency(classes, clss);

            if(idFreq < invalidNum && strFreq < invalidNum){
                if(preIF > invalidNum){
                    invalid.add(clss);
                    tmp.set(tmp.size()-1, joker);
                    continue;
                }
                invalid.add(clss);
                tmp.clear();
                continue;
            }
            if(idFreq < invalidNum) {
                invalid.add(clss);
                tmp.set(tmp.size()-1, joker);
            }
            preIF = idFreq;
        }

        invalid = invalid.stream().distinct().collect(Collectors.toList());

        tmp.clear();
        for(int i = 0;i<invalid.size()-1;i++){
            double sum = 0.0;
            String x = invalid.get(i);
            for(int j = 1;j<invalid.size();j++) sum += findSimilarity(x, invalid.get(j));
            if(sum/invalid.size() < 0.6) tmp.add(x);
        }
        invalid.removeAll(tmp);
        String t = invalid.get(0);
        t = t.replaceAll("[0-9]*", "");
        for(String s: t.split(" "))
            if(s.length() > 2) master.add(s);


        master = master.stream().distinct().collect(Collectors.toList());
        master.remove(joker);

        return master;
    }

    public String getTitleClass(List<String> classList) {
        String titClass = "";   // 제목으로 추정되는 클래스 이름
        String titTmp = "";

        List<Integer> classLen = new ArrayList<>();
        List<Double> lenAvg = new ArrayList<>();

        for (String c : classList) {
            List<String> elemList = getElem(c);
            //System.out.println(c + ": " + elemList);
            classLen.add(elemList.size());
            lenAvg.add(Math.round(elemList.stream().mapToDouble(String::length).sum() / elemList.size() * 100) / 100.0);
        }

        while (classLen.contains(0)) {
            int idx = classLen.indexOf(0);
            classList.remove(classList.get(idx));
            classLen.remove(classLen.get(idx));
            lenAvg.remove(lenAvg.get(idx));
        }

        Double mx = Collections.max(lenAvg);
        if (!Objects.equals(mx, 0)) titClass = classList.get(lenAvg.indexOf(mx));
        else {
            String title = "";
            for (String c : classList) {
                if (c.contains("num") && c.contains("date")) continue;
                if (c.contains("tit")) title = c;
                else if (c.contains("link") || c.contains("left")) titTmp = c;
            }
            if (title.equals("")) title = titTmp;
            titClass = title;
        }

        int elemLen = 0;
        if (Objects.equals(titClass, "")) {
            for (int i = 0; i < classList.size(); i++) {
                List<Element> t;
                try {
                    t = doc.getElementsByClass(classList.get(i));
                } catch (Exception e) {
                    classList.remove(i);
                    continue;
                }
                String randElem = t.get(5).text();   // 무작위 post 뽑아서
                //randElem = randElem.replaceAll(" ", "").replaceAll("\n", "");

                if (elemLen < randElem.length() && !isDigit(randElem)) {
                    titTmp = classList.get(i);
                    elemLen = randElem.length();
                }
            }
            if (Objects.equals(titClass, "")) titClass = titTmp;
            //System.out.println("num class (manual): " + numClass);
        }
        return titClass;
    }
    public String getNumClass(List<String> classList) {
        String numClass = "";
        String numTmp = "";

        List<String> numList = new ArrayList<>();
        String tmp = "";

        for (String c : classList)
            if (c.contains("num")) numList.add(c);

        if (numList.size() == 1) tmp = numList.get(0);
        else {
            for (String n : numList) {
                if (!n.contains("notice") && !n.contains("nt") && !n.contains("not") && !n.contains("reply") && !n.contains("comment")) {
                    if (n.contains("td") || n.contains("row")) return n;
                    tmp = n;
                }
            }
        }
        numClass = tmp;

        int elemLen = 0;
        if (Objects.equals(numClass, "")) {
            for (int i = 0; i < classList.size(); i++) {
                List<Element> t;
                try {
                    t = doc.getElementsByClass(classList.get(i));
                } catch (Exception e) {
                    classList.remove(i);
                    continue;
                }
                /////////////////
                String randElem = t.get(5).text();   // 무작위 post 뽑아서
                //randElem = randElem.replaceAll(" ", "").replaceAll("\n", "");

                if (isDigit(randElem) && Objects.equals(numTmp, "")) numTmp = classList.get(i);

                if (elemLen < randElem.length() && !isDigit(randElem)) {
                    elemLen = randElem.length();
                }
            }
            if (Objects.equals(numClass, "")) numClass = numTmp;
            //System.out.println("num class (manual): " + numClass);
        }

        return numClass;
    }

    public boolean login(String url, String id, String pw){
        String src = doc.outerHtml();
        String[] tmp = src.split("name=\"");


        String idName = ""; String pwName = "";
        int idIdx = -1; int pwIdx = -1;
        String loginBt = "";

        // find id, pw name tag
        for(int i =0;i<tmp.length;i++) {
            tmp[i] = tmp[i].split("\"")[0];
            if(tmp[i].contains("id")){
                if(idName.length() == 0){
                    idName = tmp[i];
                    idIdx = i;
                }
                else if(pwName.length() != 0 && pwIdx != -1){
                    idName = tmp[pwIdx-1];
                    idIdx = pwIdx-1;
                }
            }
            if(tmp[i].contains("password") || tmp[i].contains("pw")){
                if(pwName.length() == 0){
                    pwName = tmp[i];
                    pwIdx = i;
                }
                else if(idName.length() != 0 && idIdx != -1){
                    pwName = tmp[idIdx+1];
                    pwIdx = idIdx+1;
                }
            }
        }

        if(postMap.get(url).getIdName() != null && postMap.get(url).getPwName() != null) {
            if (!Objects.equals(postMap.get(url).getIdName(), idName) || !Objects.equals(postMap.get(url).getPwName(), pwName)) {
                return true;
            }
        }

        return false;
    }

    public boolean isValid(String str){
        boolean result = true;

        if(str == null || str.length() == 0)
            return false;
        else{
            for(int i = 0;i < str.length();i++){
                int c = (int) str.charAt(i);
                if(!(c > 32 && c < 58))
                    result = false;
            }
        }
        return result;
    }

    public int getLevenshteinDistance(String X, String Y) {
        int m = X.length();
        int n = Y.length();

        int[][] T = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) T[i][0] = i;
        for (int j = 1; j <= n; j++) T[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0: 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }
        return T[m][n];
    }
    public double findSimilarity(String x, String y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        double maxLength = Double.max(x.length(), y.length());
        if (maxLength > 0) {
            // 필요한 경우 선택적으로 대소문자를 무시합니다.
            return Math.round(((maxLength - getLevenshteinDistance(x, y)) / maxLength)*100)/100.0;
        }
        return 1.0;
    }

    public boolean isDigit(String n) {
        boolean answer = false;
        int i;
        for (i = 0; i < n.length(); i++) {
            if (!Character.isDigit(n.charAt(i))) break;
        }
        if (i == n.length()) answer = true;
        return answer;
    }
    public int countStr(String src, String str){
        Matcher match = Pattern.compile(str).matcher(src);
        int i = 0;
        while(match.find()){
            i+=1;
            match.group();
        }
        return i;
    }
    public int maxCount(List<String> str){
        int i = 0;
        for(String s: str){
            i = Collections.max(Arrays.asList(Collections.frequency(str, s), i));
            if(i > 30) return i;
        }
        return i;
    }
}