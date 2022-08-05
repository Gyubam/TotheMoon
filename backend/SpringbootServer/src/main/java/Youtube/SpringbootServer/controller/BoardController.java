package Youtube.SpringbootServer.controller;

import Youtube.SpringbootServer.SessionConst;
import Youtube.SpringbootServer.dto.*;
import Youtube.SpringbootServer.entity.*;
import Youtube.SpringbootServer.repository.RecordRepository;
import Youtube.SpringbootServer.service.BoardService;
import Youtube.SpringbootServer.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Math.*;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentListDTO commentListDTO;
    private final KeywordDTO keywordDTO;
    private final KeywordCommentDTO keywordCommentDTO;
    private final PercentDTO percentDTO;
    private final VideoInformationDTO videoInformationDTO;
    private final InterestListDTO interestListDTO;
    private final TimeLineListDTO timeLineListDTO;
    private final MemberService memberService;
    private final RecordRepository recordRepository;

//    //목록 조회
//    @GetMapping("/listPage")
//    public String recordList2(Model model, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember){
//        List<RecordDTO> records = boardService.findRecords(loginMember.getId());
//        model.addAttribute("records", records);
//        return "db_complete_list";
//    }


    //목록 조회(페이징기능)
    @GetMapping("/list")
    public String recordList(Model model, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                       @PageableDefault(page=0, size = 10) Pageable pageable,
                             @RequestParam(required = false,defaultValue = "") String search){
        Page<RecordDTO> recordPage = boardService.findRecordsPage(loginMember.getId(), pageable, search);
        List<RecordDTO> records = recordPage.getContent();

        int nowPage = recordPage.getPageable().getPageNumber();
        int startPage = max(nowPage - 4, 1);
        int endPage = min(nowPage + 4, recordPage.getTotalPages());
        if(endPage==0) endPage+=1;

        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        model.addAttribute("recordPage",recordPage);
        model.addAttribute("records", records);
        return "db_complete_list";
    }

    @GetMapping("/record")
    public String persistComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember){
        Record record = new Record();
        boardService.registerDB(commentListDTO,record,loginMember,keywordDTO, keywordCommentDTO, percentDTO, videoInformationDTO,interestListDTO,timeLineListDTO);
        return "redirect:/list";
    }

    //분석 1건 조회
    @GetMapping("/record/{recordId}")
    public String showComments(@PathVariable String recordId,  Model model){
        long longRecordId = Long.parseLong(recordId);
        List<CommentDTO.Response> comments = boardService.findComment(longRecordId);
        PercentDTO.Response percent = boardService.findPercent(longRecordId);
        VideoInformationDTO.Response videoInfo = boardService.findVideoInfo(longRecordId);
        List<InterestDTO.Response> interest = boardService.findInterest(longRecordId);
        List<KeywordDTO.Response> keyword = boardService.findKeyword(longRecordId);
        List<TimelineDTO.Response> timeLine = boardService.findTimeLine(longRecordId);
        List<KeywordCommentDTO.Response> keywordComments = boardService.findKeywordComment(longRecordId);
        String recordDate = boardService.findRecordCreatedDate(longRecordId);
        model.addAttribute("recordDate",recordDate);
        model.addAttribute("comments",comments);
        model.addAttribute("percent",percent);
        model.addAttribute("videoInfo",videoInfo);
        model.addAttribute("interests",interest);
        model.addAttribute("keywords",keyword);
        model.addAttribute("timelines",timeLine);
        model.addAttribute("keywordComments", keywordComments);
        return "db_complete_show";
    }

    //분석 1건 삭제
    @PostMapping("/record/delete")
    public String delete(long recordId){
        boardService.delete(recordId);
        return "redirect:/list";
    }
}
