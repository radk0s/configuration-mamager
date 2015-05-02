package controllers.communication;

public enum Urls {
	DIGITAL_OCEAN_URL{
		public String toString(){
			return "https://api.digitalocean.com/v2/droplets/";
		}
	},
	AWS_URL{
		public String toString(){
			return "https://ec2.amazonaws.com/";
		}
	},
	AWS_HOST{
		public String toString(){
			return "ec2.amazonaws.com";
		}
	}
}
