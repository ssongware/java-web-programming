package core.nmvc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.OrderComparator;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class SpringMvcController extends HttpServlet {
	private static final long serialVersionUID = -5704524104722380068L;
	private static final Logger logger = LoggerFactory.getLogger(SpringMvcController.class);
	
	private ConfigurableWebApplicationContext context;
	
	private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();
	private List<HandlerAdapter> handlerAdapters = new ArrayList<HandlerAdapter>();
	
	private ViewResolver viewResolver;
	private LocaleResolver localeResolver;
	
	@Override
	public void init() throws ServletException {
		context = new XmlWebApplicationContext();
		context.setParent(WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()));
		context.setConfigLocation(configPath());
		context.setServletContext(getServletContext());
		context.setServletConfig(getServletConfig());
		context.refresh();
		
		initHandlerMapping();
		initHandlerAdapters();
		
		viewResolver = context.getBean(ViewResolver.class);
		localeResolver = context.getBean(LocaleResolver.class);
	}
	
	private void initHandlerMapping() {
		Map<String, HandlerMapping> matchingBeans =
		        BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
		handlerMappings.addAll(matchingBeans.values());
		OrderComparator.sort(this.handlerMappings);
	}
	
	private void initHandlerAdapters() {
		Map<String, HandlerAdapter> matchingBeans =
		        BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
		handlerAdapters.addAll(matchingBeans.values());
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("Method : {}, Request URI : {}", request.getMethod(), request.getRequestURI());
		
		try {
			HandlerExecutionChain hec = null;
			for (HandlerMapping handlerMapping : handlerMappings) {
				logger.debug("Using HandlerMapping : {}", handlerMapping);
				hec = handlerMapping.getHandler(request);
				if (hec != null) {
					break;
				}
			}
			
			logger.debug("Handler : {}", hec.getHandler().getClass());
			
			ModelAndView mav = null;
			for (HandlerAdapter each : this.handlerAdapters) {
				if (each.supports(hec.getHandler())) {
					mav = each.handle(request, response, hec.getHandler());
				}
			}
			
			renderView(request, response, mav);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void renderView(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav) throws Exception {
		if (mav.getView() != null) {
			View view = mav.getView();
			view.render(mav.getModel(), request, response);
			return;
		}
		
		View view = viewResolver.resolveViewName(mav.getViewName(), localeResolver.resolveLocale(request));
		view.render(mav.getModel(), request, response);
	}
	
	private String configPath() {
		return "WEB-INF" + File.separator + getServletName() + "-servlet.xml"; 
	}
}