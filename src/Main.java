import java.io.IOException;


public class Main {
	public static void main(String[] args){
		System.out.println("hello");
		String galleryURL = "http://metmuseum.org/collection/the-collection-online/search?pg=1&amp;rpp=90&amp;what=Paintings&amp;ft=*&amp;ao=on&amp;noqs=true";
		WebScraper scraper = new WebScraper();
		try {
			scraper.webScraper();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
