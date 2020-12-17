package app.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController  {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	  
	    System.out.println("error page " + status + " / " + request.getRequestURI());
	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	        if(statusCode==HttpStatus.OK.value()) {
	        	return "fragments/general.html ::customError";
	        }
	        
	        if(statusCode == HttpStatus.NOT_FOUND.value()) {
	            return "error/404";
	        }
	        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return "error";
	        }
	    }
	    return "error";
	}

    @Override
    public String getErrorPath() {
        return null;
    }
}