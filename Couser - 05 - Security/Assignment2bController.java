
package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import retrofit.http.Body;

import com.google.common.collect.Lists;

@Controller
public class Assignment2bController {
		
	@Autowired
	private VideoRepository videos;

	//GET /video
	//Returns the list of videos that have been added to the server
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(){
		return Lists.newArrayList(videos.findAll());
	}
	
	//POST /video
	//Each video should be assigned an owner based on the identity of the Principal that created the Video
	//If a Video already exists, it should not be overwritten unless the name of the authenticated Principal matches the name of the owner of the Video
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<Video> addVideo(@RequestBody Video video, Principal p){
			
		Collection<Video> resultVideosSameName = findByTitle(video.getName());
		
		//If a Video already exists		
		if (!resultVideosSameName.isEmpty()) {

			// if the name of the authenticated Principal matches the name of the owner of the Video overwrite it
			String owner = resultVideosSameName.iterator().next().getOwner();
			String uploader = p.getName();

			if(owner.equals(uploader)){
							
				//return videos.save(video);	
				//remove video to replace to new one with the same name
				videos.delete(resultVideosSameName.iterator().next());
				
				video.setOwner(p.getName());
				return new ResponseEntity<Video>(videos.save(video), HttpStatus.OK);		

			}
			else {// Not overwrite
				return new ResponseEntity<Video>(HttpStatus.BAD_REQUEST);
				
							
			}			
		}
					
		//if video not exist yet
	    //assign an owner based on the identity of the Principal that created the Video
		//and save video in repository
		else {			
			video.setOwner(p.getName());
			return new ResponseEntity<Video>(videos.save(video), HttpStatus.OK);		

		}	
		
	}
	
	//GET /video/{id}
	//Returns the video with the given id.
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video findById(@PathVariable("id") long id){
		return videos.findOne(id);
	}
	
	//POST /video/{id}/like
	//Allows a user to like a video. Returns 200 Ok on success, 
	//404 if the video is not found, or 400 if the user has already liked the video.
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<Void> likeVideo(@PathVariable("id") long id
			, @Body String body
			, Principal p){
		if(videos.exists(id)){
			Video video = videos.findOne(id);
			if(video.userAlreadyLiked(p.getName())){
				
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
			else{
				addUserLike(video, p.getName());

				videos.save(video);
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
		}
		else {
			
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	//POST /video/{id}/unlike
	//Allows a user to unlike a video that he/she previously liked. Returns 200 OK on success, 
	//404 if the video is not found, and a 400 if the user has not previously liked the specified video.
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<Void> unlikeVideo(@PathVariable("id") long id
			, @Body String body
			, Principal p){
		
		Video video = videos.findOne(id);
		if(videos.exists(id)){
			if(video.userAlreadyLiked(p.getName())){
				removeUserLike(video, p.getName());
				
				videos.save(video);
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			else{
				
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	//GET /video/{id}/likedby
	//Returns a list of the string usernames of the users that have liked the specified video. 
	//If the video is not found, a 404 error should be generated.
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH+"/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<Collection<String>> getUsersWhoLikedVideo(@PathVariable("id")long id){
		if(videos.exists(id)){
			
			return new ResponseEntity<Collection<String>>(videos.findOne(id).getUsersLiked(),HttpStatus.OK);		
		}
		else {
			
			return new ResponseEntity<Collection<String>>(HttpStatus.NOT_FOUND);
		}
	}
	

	
	//utils
	public Collection<Video> findByTitle(String title){
		return videos.findByName(title);
	}
	
		
	public void addUserLike(Video v, String user){
		
        v.getUsersLiked().add(user);
        v.setLikes(v.getLikes() + 1);
	}
	
	public void removeUserLike(Video v, String user){
		
        v.getUsersLiked().remove(user);
        v.setLikes(v.getLikes() - 1);
				
	}
	
	
	
}
