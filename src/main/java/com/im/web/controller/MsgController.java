package com.im.web.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.im.web.base.Page;
import com.im.web.entity.Message;
import com.im.web.service.MessageService;

@Controller
@RequestMapping("/msg")
public class MsgController {
	@Autowired
	private MessageService msgService;
	
	@RequestMapping(value = "/msgcomit", method = RequestMethod.GET)
	public void MessagemitIndex() {
		
	}
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody Page<Message> getAll(@RequestParam("page") String page,@RequestParam("limit") String pageSize) {
		Page<Message> Messages = msgService.queryAll(Integer.parseInt(page), Integer.parseInt(pageSize));
		return Messages;
	}
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody Page<Message> search(@RequestParam("page") String page,@RequestParam("limit") String pageSize){
		Page<Message> Messages = msgService.search(Integer.parseInt(page), Integer.parseInt(pageSize));
		return Messages;
	}
	@RequestMapping(value = "/getMessage/{no}", method = RequestMethod.GET)
	public @ResponseBody Message getProj(@PathVariable("no") String no) {
		Message Message = msgService.queryMsgById(no);
		return Message;
	}

	
	@RequestMapping(value = "/add", method = RequestMethod.POST )
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void add(@RequestBody String item) {
		Message Message = (Message)JsonParser.parseJsonStrToBean(URLDecoder.decode(item),Message.class);
		try {
			this.msgService.addMsg(Message,null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@PathVariable("id")String id,@RequestBody String sys) {
		Message Message = (Message)JsonParser.parseJsonStrToBean(sys,Message.class);
		this.msgService.update(Message);
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		this.msgService.delete(Integer.parseInt(id));
	}
}
