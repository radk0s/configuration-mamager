package controllers.communication;

public enum Urls {
	DIGITAL_OCEAN_URL{
		public String toString(){
			return "https://api.digitalocean.com/v2/droplets/";
		}
	}
}
