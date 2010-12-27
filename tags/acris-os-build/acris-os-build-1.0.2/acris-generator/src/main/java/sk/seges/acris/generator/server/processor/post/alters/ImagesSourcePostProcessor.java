package sk.seges.acris.generator.server.processor.post.alters;

import org.htmlparser.Node;
import org.htmlparser.tags.ImageTag;
import org.springframework.stereotype.Component;

@Component
public class ImagesSourcePostProcessor extends AbstractPathPostProcessor {

	@Override
	public boolean supports(Node node) {
		
		if (node instanceof ImageTag) {
			return isPathRelative(getPath(node));
		}

		return false;
	}

	@Override
	protected String getPath(Node node) {
		ImageTag imageTag = (ImageTag)node;
		return imageTag.extractImageLocn();
	}

	@Override
	protected void setPath(Node node, String path) {
		ImageTag imageTag = (ImageTag)node;
		imageTag.setImageURL(path);
	}
}