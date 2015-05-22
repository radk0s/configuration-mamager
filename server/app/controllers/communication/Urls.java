package controllers.communication;

public enum Urls {
	DIGITAL_OCEAN_URL{
		public String toString(){
			return "https://api.digitalocean.com/v2/droplets/";
		}
	},
	DIGITAL_OCEAN_REGIONS{
		public String toString() {
			return "https://api.digitalocean.com/v2/regions";
		}
	},
	DIGITAL_OCEAN_IMAGES{
		public String toString() {
			return "https://api.digitalocean.com/v2/images?type=distribution";
		}
	},
	DIGITAL_OCEAN_SIZES{
		public String toString() {
			return "https://api.digitalocean.com/v2/sizes";
		}
	},
	AWS_URL{
		public String toString(){
			return "ec2.eu-central-1.amazonaws.com";
		}
	},
	AWS_HOST{
		public String toString(){
			return "ec2.amazonaws.com";
		}
	}
}
