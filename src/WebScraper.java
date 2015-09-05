
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebScraper {
	
	private static final String folderPath = "C:\\Users\\Gabriel\\Documents\\work\\CulturalCapital\\";
	
	/*
	 * Sends all of the search pages for the MET's websites into connector method
	 */
	public void webScraper() throws IOException{
		
		for (int i = 1; i<432; i++){
			System.out.println("%%%%%%%%%%%%%%%" + i + "%%%%%%%%%%%%%%%%%%%");
			String galleryURL = 
					"http://metmuseum.org/collection/the-collection-online/search?what=Paintings&ft=*&rpp=30&pg=" + i;
			ArrayList<String> linkList = connector(galleryURL);
			for (String paintingLink : linkList){
				paintingScraper("http://metmuseum.org" + paintingLink);
			}
		}
	}
		
	
	/*
	 * Connects Jsoup to each MET search page and gets the link for each painting
	 * Sends the link to each painting to paintingScraper(link)
	 * 	
	 */
	public ArrayList<String> connector(String galleryURL){
		Document doc;
		try {
			doc = Jsoup.connect(galleryURL).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			doc = null;
			e.printStackTrace();
		}
		Elements grid = doc.getElementsByClass("grid-results-thumbnail");
		Elements linksHTML = grid.select("a[href]");
		ArrayList<String> linkList = new ArrayList<String>();
		for (Element link : linksHTML){
			linkList.add(link.attr("href"));
		}
		return linkList;
		
	}
	/*
	 * Downloads one painting
	 */
	public void paintingScraper(String paintingLink) throws IOException{
		Document doc;
		try {
			doc = Jsoup.connect(paintingLink).get();
		} catch (IOException e){
			doc = null;
			e.printStackTrace();
		}
		Elements imgContainer = doc.getElementsByClass("download");
		String imgURL = imgContainer.attr("href");
		for (int i = 0; i<imgURL.length(); i++){
			if (Character.isWhitespace(imgURL.charAt(i))){
				return;
			}
		}
		
		/*
		 * The following parses the single image for 
		 * Title
		 * Artist
		 * Date
		 * Culture
		 * To be added to the image file properties
		 */
		Elements imageInfo = doc.getElementsByClass("tombstone-container");
		String allInfo = imageInfo.select(".tombstone").text();
		
		
		String artist = null;
		int endArtist = 0;
		String date = null;
		int startDate = 0;
		int endDate = 0;
		String culture = null;
		int startCulture = 0;
		int endCulture = 0;
		/*
		 * Parses the info for the Title
		 */
		String title = imageInfo.select("h2").text();
		title = title.replaceAll("[^A-Za-z0-9 ]","").trim();

		/*
		 * Parses the info for the Artist
		 */
		for (int i = 9; i<allInfo.length(); i++){
			if (!allInfo.contains("Artist:")){
				endArtist = 0;
				break;
			}
			if (allInfo.substring(8, i).contains(": ")){
				endArtist = i - 2;
				artist = allInfo.substring(8, endArtist).trim();
				if (artist.contains("(")){
					for (int h = 0; h<artist.length(); h++){
						if (artist.substring(0,h).contains("(")){
							endArtist = h-1;
							artist = artist.substring(0,h-1).trim();
							break;
						}
					}
				}
				else{
					break;
					}
			}
		}
		
		/*
		 * parses the info for the Date
		 */
		for (int j = endArtist; j<allInfo.length(); j++){
			if (allInfo.substring(endArtist,j).contains("Date: ")){	
				startDate = j;
				for (int k = startDate; k<allInfo.length(); k++){
					if (allInfo.substring(startDate, k).contains("Culture: ")){
						endDate = k - 10;
						date = allInfo.substring(startDate, endDate);	
						break;
					}
					if (allInfo.substring(startDate, k).contains("Medium: ")){
						endDate = k-9;
						date = allInfo.substring(startDate, endDate);	
						break;
					}
					if (allInfo.substring(startDate, k).contains("Dimensions: ")){
						endDate = k-13;
						date = allInfo.substring(startDate, endDate);	
						break;
					}
				}
				break;
			}
		}
		/*
		 * Parses the info for the culture
		 */
		for (int l = endDate; l<allInfo.length(); l++){
			if (allInfo.substring(endDate, l).contains("Culture: ")){
				startCulture = l;
				for (int m = startCulture; m<allInfo.length(); m++){
					if (allInfo.substring(startCulture, m).contains("Medium")){
						endCulture = m - 6;
						culture = allInfo.substring(startCulture, endCulture).trim();
						break;
					}
					if (allInfo.substring(startCulture, m).contains("Geography")){
						endCulture = m - 10;
						culture = allInfo.substring(startCulture, endCulture).trim();
						break;
					}
				}
			break;
			}
		}
		
		if (imgURL.equals(null) || imgURL.isEmpty()){
			System.out.println("No info!");
			System.out.println("-----------------------------------------------");
			return;
		}
		else {
			System.out.println("artist: " + artist);
			System.out.println("title: " + title);
			System.out.println("date: " +date);
			System.out.println("culture: " + culture);
			System.out.println("url: " + imgURL);
		}
		
		String artistFile = null;
		String titleFile = null;
		if (artist == null){
			artist = "none";
			System.out.println("THIS SHOULD PRINT");
		}
		if (title == null){
			title = "none";
		}
		
		artistFile = artist.replaceAll("[^A-Za-z0-9]", "");
		System.out.println("artistfIle: " + artistFile);
		titleFile = title.replaceAll("[^A-Za-z0-9]", "");
		System.out.println("titlefile: " + titleFile);
		
		/*
		 * The following copies the file into my directory
		 */
		
		String destinationFile = folderPath + artistFile + "-" + titleFile + ".jpg";
		System.out.println(destinationFile);
		System.out.println("-----------------------------------------------");

		if (imgURL != null){
			URL url = new URL(imgURL);
			InputStream in = url.openStream();
			
			OutputStream out = new FileOutputStream(destinationFile);	
			
			byte[] b = new byte[2048];
			int length;
			while((length = in.read(b)) != -1) {
				out.write(b, 0, length);
			}
			in.close();
			out.close();
		}
		
		
			
		
	}
		
}
