package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.BoardDto;
import com.example.NewPostAlarmBot.DTO.CrawlDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jvnet.hk2.annotations.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Transactional
public class BoardEditor {
    
    private final CrawlService crawlService;
    private final BoardService boardService;

    public Document doc;

    public static Map<String, CrawlDto> crawlMap = new ConcurrentHashMap<>();
    public static Map<String, BoardDto> boardList = new ConcurrentHashMap<>();

    public BoardEditor(CrawlService crawlService, BoardService boardService) {
        this.crawlService = crawlService;
        this.boardService = boardService;

    }

    public void init(String url){
        if(!crawlMap.containsKey(url)){
            CrawlDto crawlDto = new CrawlDto();
            crawlDto.setUrl(url);
            crawlDto.setTitleClass("");
            crawlDto.setNumClass("");
            crawlDto.setUrlTitle(doc.title());
            crawlMap.put(url, crawlDto);
//            System.out.println("BoardRepo size: " + boardService.findAll().size());
//            for(BoardDto b: boardService.findAll()) System.out.println(b.url);
        }
    }

    // not use
//    public void pattern(){
//        String src = doc
//        String[] tmp = src.split("</");
//
//        Stack<String> tag = new Stack<>();
//        List<String> list = new ArrayList<>();
//        //Queue<String> tag = new ArrayDeque<>();
//
//        while(src.contains("<!"))
//            src = src.replace(src.substring(src.indexOf("<!"), src.indexOf("-->")+"-->".length()), "");
//
//        if(src.contains("<tbody") && src.contains("</tbody>"))
//            src = src.substring(src.indexOf("<tbody"), src.indexOf("</tbody>")+("</tbody>").length());
//
//
//        //System.out.println(src);
//
//        for(int i =0;i<tmp.length;i++) {
//            tmp[i] = tmp[i].split(">")[0];
//            if(tmp[i].contains("script") || tmp[i].contains("html")) continue;
//
//            tag.push(tmp[i]);
//
//            //System.out.println("["+tag.peek()+"]");
//        }
//        //System.out.println(tag);
//    }


    public List<String> boardUpdate(String url){
        List<String> newTitle = new ArrayList<>();
        List<String> curTitle = findPostList(url);
        CrawlDto crawlDto = crawlMap.get(url);
        String topTitle = crawlDto.getTopTitle();
        //System.out.println(topTitle);

        //for(String t: curTitle) System.out.println(t);
        //System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");

        if(crawlDto.getTopTitle() != null){
            for (String cur : curTitle) {
                if (!topTitle.equals(cur)) {
                    newTitle.add(cur);
//                    System.out.println(cur);
                } else break;
            }
            if (newTitle.size() > 0) {
                crawlDto.setTopTitle(newTitle.get(0));
                if(crawlMap.get(url).getBoardSize() == 0) {
                    crawlMap.get(url).setBoardSize(curTitle.size());
                    if(curTitle.size() == newTitle.size()) newTitle.clear();
                }
                else if(crawlMap.get(url).getBoardSize() == newTitle.size())
                    newTitle.clear(); // 새로운 제목 리스트여도 개수가 전체 게시글 개수와 같으면 보내지 않음
            }
        }
        else crawlDto.setTopTitle(curTitle.get(0));
        crawlMap.put(url, crawlDto);

//        System.out.println("crawlMap " + crawlMap.size());
//        System.out.println(crawlMap.toString());
//        System.out.println("BoardList " + boardList.size());
//        System.out.println(boardList.toString());
//        System.out.println("\n");

        return newTitle;
    }

    public List<String> findPostList(String url){
        BoardDto board;

        List<String> titleList = new ArrayList<>();
        List<String> numList = new ArrayList<>();
        List<String> titListWithoutNotice = new ArrayList<>();

        if(boardList.keySet().contains(url)) {
            if (boardList.get(url).title.equals(""))
                findPostClass(url);
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
        String src = doc.outerHtml();
        while(src.contains("<!--") && src.contains("-->"))
            src = src.replace(src.substring(src.indexOf("<!--"), src.indexOf("-->")+"-->".length()), "");


        // 게시글이 존재할 가장 유력한 태그 장소 tbody
        if(src.contains("<tbody") && src.contains("</tbody>")) {
            int tbodyNum = countStr(src, "<tbody");
            if(tbodyNum == 1)
                src = src.substring(src.indexOf("<tbody"), src.indexOf("</tbody>") + ("</tbody>").length());
            else{
                String docTmp = "";
                String docTmp2 = "";
                for(int i = 0;i < tbodyNum;i++){
                    docTmp2 = src.substring(src.indexOf("<tbody"), src.indexOf("</tbody>") + ("</tbody>").length());
                    if(docTmp.length() < docTmp2.length()) docTmp = docTmp2;
                    src = src.replace(docTmp2, "");
                }
                src = docTmp;
            }
        }
        else src = src.substring(src.indexOf("<body"), src.indexOf("</body>")+("</body>").length());

//        System.out.println("\nㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ\n");
//        System.out.println(src);
//        System.out.println("\nㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ\n");


//          // 반복되는 태그 찾는 프로세스 (포기 상태)
//        String doc2 = src.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
//        List<String> tagList = new ArrayList<>();
//        if(doc2.contains("</")){
//            String[] tagTmp = doc2.split("</");
//
//            for(String s: tagTmp){
//                if(!s.contains("><")) continue;
//                String tag = s.substring(0, s.indexOf("><"));
//                //System.out.println(s);
//
//                if(s.contains(tag+"><"+tag) && !tag.equals("script")) tagList.add(tag);
//            }
//        }
//        String tagStr = String.join(" ", tagList);
//        List<String> it = new ArrayList<>();
//        Map<String, Integer> cycle = new HashMap<>();
//
//        System.out.println(tagList);
//
//        Iterator<String> iter = tagList.iterator();
//
//        for(int i =0;i<tagList.size();i++){
//            if(i+1 < tagList.size()) {
//                if(it.isEmpty()) it.add(tagList.get(i));
//                else if (!Objects.equals(tagList.get(i), tagList.get(i + 1)) && !Objects.equals(it.get(0), tagList.get(i))) {
//
//                    it.add(tagList.get(i));
//
//                } else{
//                    while(it.size() > 1){
//                        String iterStr = String.join(" ", it);
//
//                        int n = tagStr.split(iterStr).length - 1;
//                        System.out.println(iterStr + ": " + n);
//
//                        if(n > 7){
//                            cycle.put(iterStr, n);
//                            break;
//                        }else it.remove(0);
//                    }
//                    it.clear();
//                }
//            }
//
//        }
//        System.out.println(cycle);

        String[] tmp = src.split("class=\"");

        for(int i = 0;i<tmp.length;i++)
            tmp[i] = tmp[i].split("\"")[0];

        HashSet<String> tmpSet = new HashSet<>(Arrays.asList(tmp));
        tmpSet.remove(tmp[0]);
        List<String> nameSet = new ArrayList<>(tmpSet);

        //System.out.println(nameSet);

        List<Integer> classFreq = new ArrayList<>(); // 중복 제거 클래스 이름 빈도
        List<String> classNameList = new ArrayList<>(); // 중복 제거 클래스 이름 목록

        for (String name : nameSet) {
            int freq = Collections.frequency(Arrays.asList(tmp), name);
            if (freq > 8 && !Objects.equals(name, " ") && !name.contains("{")) {
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


        for(int i = 0;i<freqSet.length;i++){
            int freq = Collections.frequency(classFreq, freqSet[i]);
            freqOfFreq.add(freq);
        }

//        System.out.println(classNameList);
//        System.out.println(classFreq);
//        System.out.println(freqOfFreq + ", " + Arrays.toString(freqSet));

        int maxFreq = freqSet[freqOfFreq.indexOf(Collections.max(freqOfFreq))]; // 클래스 이름 빈도의 빈도의 최대값
        //int secFreq = freqSet[freqOfFreq.indexOf(s)];

        List<String> boardClassList = new ArrayList<String>(); // 게시글 정보 클래스 이름 리스트
        List<String> boardClassListSec = new ArrayList<String>();

        for(int i = 0;i < classFreq.size();i++){
            if (classFreq.get(i) == maxFreq)
                boardClassList.add(classNameList.get(i));
//            if (classFreq.get(i) == secFreq)
//                boardClassListSec.add(classNameList.get(i));
        }

        //System.out.println(boardClassList + ", " + boardClassListSec);

        String titClass = "";   // 제목으로 추정되는 클래스 이름
        String numClass = "";
        String titTmp = "";
        String numTmp = "";

        titClass = getTitleClass(boardClassList);
        numClass = getNumClass(nameSet);
        //System.out.println("num class (getNumClass): " + numClass);

        int elemLen = 0;

        if(Objects.equals(titClass, "") || Objects.equals(numClass, "")) {
            for (int i = 0; i < boardClassList.size(); i++) {
                List<Element> t;
                try {
                    t = doc.getElementsByClass(boardClassList.get(i));
                }catch (Exception e){
                    boardClassList.remove(i);
                    continue;
                }
                String tmp3 = t.get(3).text();   // 무작위 post 뽑아서
                //tmp3 = tmp3.replaceAll(" ", "").replaceAll("\n", "");

                if (isDigit(tmp3) && Objects.equals(numTmp, "")) numTmp = boardClassList.get(i);

                if (elemLen < tmp3.length() && !isDigit(tmp3)) {
                    titTmp = boardClassList.get(i);
                    elemLen = tmp3.length();
                }
            }
            if(Objects.equals(titClass, "")) titClass = titTmp;
            if(Objects.equals(numClass, "")) numClass = numTmp;
            //System.out.println("num class (manual): " + numClass);
        }

        crawlMap.get(url).setTitleClass(titClass);
        crawlMap.get(url).setNumClass(numClass);

        BoardDto dto = new BoardDto(url, titClass, numClass);
        boardList.put(url, dto);
        boardService.save(dto);
    }

    public List<String> getElem(String clss){
        String src = this.doc.outerHtml();
        List<String> elemList = new ArrayList<>();

        if(src.contains("<tbody") && src.contains("</tbody>")) {
            int tbodyNum = countStr(src, "<tbody");
            if(tbodyNum == 1)
                src = src.substring(src.indexOf("<tbody"), src.indexOf("</tbody>") + ("</tbody>").length());
            else{
                String docTmp = "";
                String docTmp2 = "";
                for(int i = 0;i < tbodyNum;i++){
                    docTmp2 = src.substring(src.indexOf("<tbody"), src.indexOf("</tbody>") + ("</tbody>").length());
                    if(docTmp.length() < docTmp2.length()) docTmp = docTmp2;
                    src = src.replace(docTmp2, "");
                }
                src = docTmp;
            }
        }
        else src = src.substring(src.indexOf("<body"), src.indexOf("</body>")+("</body>").length());

        String[] tmp = src.split(clss+"\">");

        for(int i = 1; i < tmp.length; i++){
            tmp[i] = tmp[i].replaceAll("\n", "").replaceAll("\t", "");
//            if(clss.contains("num")) {
//                System.out.println(tmp[i]);
//            }

            while(tmp[i].contains("<") && tmp[i].contains(">") && !tmp[i].contains("<b")){    // 제목 태그 뒤로 꺽쇠 안 문자 지우다가 꺽쇠 밖의 문자를 만나면 저장
                tmp[i] = tmp[i].replace(tmp[i].substring(tmp[i].indexOf("<"), tmp[i].indexOf(">")+1), "");
                while(tmp[i].indexOf("  ") == 0) tmp[i] = tmp[i].replace("  ", "");   // 첫 글자가 공백이면 계속 지워나감
                //System.out.println("["+tmp[i]+"]");
                if(tmp[i].indexOf("<") != 0) {
                    String elem = tmp[i].replace(tmp[i].substring(tmp[i].indexOf("<"), tmp[i].length()), "");
                    if(clss.contains("num")) elem = elem.replaceAll(" ", "");
                    //System.out.println(elem);
                    elemList.add(elem);
                    //System.out.println(tmp[i].replace(tmp[i].substring(tmp[i].indexOf("<"), tmp[i].length()), ""));
                    break;
                }
            }

        }
        return elemList;
    }

    public List<String> getTitleList(String url){
        String titClass = crawlMap.get(url).getTitleClass();
        String numClass = crawlMap.get(url).getNumClass();
        List<String> title = new ArrayList<>();
        List<String> num = new ArrayList<>();

        try {
            title = doc.getElementsByClass(titClass).stream().map(Element::text).collect(Collectors.toList());
            num = doc.getElementsByClass(numClass).stream().map(Element::text).collect(Collectors.toList());
        }catch (Exception e){
            title = getTitleListManual2(titClass);
        }

        for(int i = 0;i<num.size();i++){
            if(isDigit(num.get(i))){
                
            }
        }

        return title;
    }

    public List<String> getTitleListManual2(String name){
        String src = doc.outerHtml();

        while(src.contains("<strong") && src.contains("</strong>")) {
            try {
                //System.out.println("<strong>" + src.indexOf("<strong>"));
                src = src.replace(src.substring(src.indexOf("<strong"), src.indexOf("</strong>")+9), "");
            }catch (Exception ignored){break;}
        }

        while(src.contains("<b") && src.contains("</b>")){
            try{
                //System.out.println("<b>" + src.indexOf("<b>"));
                src = src.replace(src.substring(src.indexOf("<b"), src.indexOf("</b>")+4), "");
            }catch (Exception ignored){break;}
        }

        src = src.replaceAll("\t", "");
        String[] tmp = src.split("class=\"" + name + "\"");

//        for(String t: tmp) {
//            System.out.println(t);
//            System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
//        }

        List<String> titleList = new ArrayList<String>();
        String title = "";

        for(int i = 1;i < tmp.length-1;i++){
            String[] tmp2 = tmp[i].split(">");

            for(int j = 0;j < tmp2.length;j++){
                if(!tmp2[j].contains("=\"") && tmp2[j].indexOf("<") != 0 && !tmp2[j].equals(" ") && tmp2[j].length() != 0 && title.length() < tmp2[j].length()){
                    title = tmp2[j];
                }
            }
            if(!isValid(title)) titleList.add(title.substring(0, title.indexOf("<")));
            title = "";
        }
//        for(String t: txt) {
//            System.out.println(t);
//        }
        return titleList;
    }

    public void findNewTitle(String url, List<String> newTitle){
        int swc = 0;
        newTitle.clear();
        try{
            String preTitle = stringToList(crawlService.findByUrl(url).getNewTitle()).get(0);
            List<String> curTitle = getTitleListManual2(url);
            //System.out.println(preTitle);

            for(String cur: curTitle){
                if(!preTitle.equals(cur)){
                    newTitle.add(cur);
                    //System.out.println(cur);
                }
                else break;
            }
            //System.out.println("newTitle num: " + newTitle.size());

            CrawlDto crawlDto = crawlService.findByUrl(url);


            if(newTitle.size() == 0) {
                crawlDto.setNewTitle(crawlDto.getTopTitle());
            }
            else if (newTitle.size() > 6) {
                crawlDto.setTopTitle(stringToList(crawlDto.getNewTitle()).get(0));
                crawlDto.setNewTitle(newTitle.get(0));
            }
            else {
                crawlDto.setTopTitle(preTitle);
                crawlDto.setNewTitle(listToString(newTitle));
            }

            crawlService.save(crawlDto);
            List<String> tmp = stringToList(crawlDto.getNewTitle());
            for(String t: tmp){
                //System.out.println(t);
            }

        }catch (Exception e){
            List<String> tmp = getTitleListManual2(url);

            CrawlDto crawlDto = crawlService.findByUrl(url);

            crawlDto.setNewTitle(tmp.get(0));
            crawlDto.setUrl(url);
            crawlService.save(crawlDto);
        }
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

        if(crawlMap.get(url).getIdName() != null && crawlMap.get(url).getPwName() != null) {
            if (!Objects.equals(crawlMap.get(url).getIdName(), idName) || !Objects.equals(crawlMap.get(url).getPwName(), pwName)) {
                return true;
            }
        }

//        System.out.println(idName + " " + pwName);

//        tmp = src.split("<");
//        List<String> elem = new ArrayList<>();

//        for(int i =0;i<tmp.length;i++) {
//            tmp[i] = tmp[i].replaceAll("\n", "");
//            if(tmp[i].indexOf("input") == 0 || tmp[i].indexOf("button") == 0){
//                if(tmp[i].contains("로그인") && tmp[i].contains("submit")){
//                    loginBt = tmp[i].split("class=\"")[1].split("\"")[0];
//                }
//                elem.add(tmp[i]);
//                System.out.println("[" + tmp[i] + "]");
//            }
//        }

//        if(idName.length() != 0 && pwName.length() != 0){
//            driver.findElement(By.name(idName)).sendKeys(id);
//            driver.findElement(By.name(pwName)).sendKeys(pw + Keys.ENTER);
//            try{
//                if (!driver.switchTo().alert().getText().contains("no such alert")) {
//                    driver.switchTo().alert().accept();
//                    driver.switchTo().defaultContent();
//                    //driver.switchTo().
//                    return false;
//                }
//
//            }catch (Exception e){
//                crawlMap.get(url).setIdName(idName);
//                crawlMap.get(url).setPwName(pwName);
//                return true;
//            }
//        }

//        System.out.println(idName + " " + pwName);

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

    public List<String> stringToList(String title){
        List<String> tmp = new ArrayList<>();
        if(!title.contains("§§")) {
            tmp.add(title);
            return tmp;
        }
        else {
            String[] tmp2 = title.split("§§");
            for (String t : tmp2)
                if (t.length() != 0) tmp.add(t);
        }
        return tmp;
    }

    public String listToString(List<String> title){
        if(title.size() == 1) return title.get(0);
        String[] tmp = title.toArray(new String[0]);
        return String.join("§§", tmp);
    }

    public boolean isDigit(String n){
        boolean answer = false;
        int i;
        for(i = 0;i<n.length();i++){
            if(!Character.isDigit(n.charAt(i))) break;
        }
        if(i == n.length()) answer = true;
        return answer;
    }

    public String getTitleClass(List<String> classList){
        String title = "";
        String tmp = "";
        for(String c: classList) {
            if(c.contains("num") && c.contains("date")) continue;
            if (c.contains("tit")) title = c;
            else if (c.contains("link") || c.contains("left")) tmp = c;
        }
        if(title.equals("")) title = tmp;
        return title;
    }

    public String getNumClass(List<String> classList){
        List<String> numList = new ArrayList<>();
        String tmp = "";

        for(String c: classList) {
            if (c.contains("num")) numList.add(c);
        }
        //System.out.println("num list: " + numList);

        if(numList.size() == 1) tmp = numList.get(0);
        else{
            for(String n: numList){
                //System.out.println(n);
                if(!n.contains("notice") && !n.contains("nt") && !n.contains("not") && !n.contains("reply") && !n.contains("comment")) {
                    if(n.contains("td") || n.contains("row")) return n;
                    tmp = n;
                }
            }
        }
        return tmp;
    }

    public int countStr(String src, String str){
        return (int) (src.length() - src.replace(str, "").length())/str.length();
    }

    public Document getDoc(String url) throws IOException {
        doc = Jsoup.connect(url)
                .header("content-type", "application/json;charset=UTF-8")
                .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .get();
        return doc;
    }

}