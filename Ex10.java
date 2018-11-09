/*  
   basic framework for drawing textured triangles
*/

import static org.lwjgl.glfw.GLFW.*;  // for key codes

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

public class Ex10 extends Basic
{
  public static void main(String[] args)
  {
    if( args.length != 0 ){
      System.out.println("Usage:  j Ex10");
      System.exit(1);
    }

    Ex10 app = new Ex10( "", 0, 1200, 600, 30 );

    app.start();

  }// main

  // instance variables 

  private Camera camera;        // first-person view
  private MapViewer mapViewer;  // map view

  private Soups mutableSoups, frozenSoups;

  private FirePyramid pyramid, b1, b2, b3, b4, b5, b6, b7, b8, b9, sky;
  private Car car1, car2, car3, car4;
  private double speed = 0.1;
  private int count=0;
  private int count2 = 0;
  private double carSpeed = 0;
  private double degrees = 90;
  private double radians = Math.toRadians(degrees);
  // construct basic application with given title, pixel width and height
  // of drawing area, frames per second, and name of world data file
  public Ex10( String appTitle, int windowShift, int pw, int ph, int fps )
  {
    super( appTitle, windowShift, pw, ph, (long) ((1.0/fps)*1000000000) );

    Pic.init();      // load all the textures
    Util.init();     // set up single large buffer for soup use

  }// Ex10 constructor

  protected void init()
  {
    OpenGL.init();

    // activate all the textures
    for( int k=0; k<Pic.size(); k++ ){
      OpenGL.loadTexture( Pic.get(k) );
      System.out.println("loaded texture number " + k );
    }

    OpenGL.setBackColor( 0, 0, 0 );

    camera = new Camera( 0, 0, 
                          600, 600,        // first person viewport within pixel grid
                         -.1, .1, -.1, .1, .4, 1000,  // frustum (camera shape)
                          49.5, 0, 2,           // eye point
                          90, 0 );           // azimuth, altitude

    // set up map view stuff
    mapViewer = new MapViewer( 600, 0, 600, 600, 0, 100, 0, 100, -500, 1 );
    
    // set up frozenSoups for display once and for all
    frozenSoups = new Soups( Pic.size() );
 
    								  
	// grass tiles on ground
	for(int xFloor = 0; xFloor <= 9; xFloor++){
		for(int yFloor = 0; yFloor <= 9; yFloor++){
			int x1 = xFloor*10;
			int x2 = 10+(xFloor*10);
			int y1 = yFloor*10;
			int y2 = 10+(yFloor*10);
			frozenSoups.addTri( new Triangle (	new Vertex(x1, y1, 0, 0,0),
												new Vertex(x2, y1, 0, 1,0),
												new Vertex(x2, y2, 0, 1,1),
												2 ) );
			frozenSoups.addTri( new Triangle (	new Vertex(x1, y1, 0, 0,0),
												new Vertex(x2, y2, 0, 1,1),
												new Vertex(x1, y2, 0, 0,1),
												2 ) );
			
		}
	}

    frozenSoups.sortByTexture();
	
	// make car
    pyramid = new FirePyramid(3, 16, 3, 3, 3);
      pyramid.scaleBy( 2, 4, 0.4 );
      pyramid.translateBy( 50, 0, 0.6 );
	  
	car1 = new Car();
	  car1.scaleBy(2, 4, 2);
	  car1.rotateBy(-90, 0, 0, 1);
	  car1.translateBy(10, 10, 0);
	
	car2 = new Car();
	  car2.scaleBy(2, 4, 2);
	  car2.translateBy(65, 10, 0);
	car3 = new Car();
	  car3.scaleBy(2, 4, 2);
	  car3.rotateBy(180, 0, 0, 1);
	  car3.translateBy(40, 90, 0);
	
	car4 = new Car();
	  car4.scaleBy(2, 4, 2);
	  car4.rotateBy(-90, 0, 0, 1);
	  car4.translateBy(12, 42, 0);
	
	// make buildings
	build();  
	
	  
	//myCar = new FirePyramid();

  }// init
  
  private void build(){
	b1 = new FirePyramid(11, 12, 13, 14, 15);
	  b1.scaleBy(5,5,15);
	  b1.translateBy(20, 80, 0);
	b2 = new FirePyramid(11, 12, 13, 14, 15);
	  b2.scaleBy(5,5,10);
	  b2.translateBy(50, 80, 0);
	b3 = new FirePyramid(11, 12, 13, 14, 15);
	  b3.scaleBy(5,5,15);
	  b3.translateBy(80, 80, 0);
	b4 = new FirePyramid(11, 12, 13, 14, 15);
	  b4.scaleBy(5,5,10);
	  b4.translateBy(20, 50, 0);
	b5 = new FirePyramid(11, 12, 13, 14, 15);
	  b5.scaleBy(5,5,15);
	  b5.translateBy(50, 50, 0);
	b6 = new FirePyramid(11, 12, 13, 14, 15);
	  b6.scaleBy(5,5,10);
	  b6.translateBy(80, 50, 0);
	b7 = new FirePyramid(11, 12, 13, 14, 15);
	  b7.scaleBy(5,5,15);
	  b7.translateBy(20, 20, 0);
	b8 = new FirePyramid(11, 12, 13, 14, 15);
	  b8.scaleBy(5,5,10);
	  b8.translateBy(50, 38, 0);
	b9 = new FirePyramid(11, 12, 13, 14, 15);
	  b9.scaleBy(5,5,15);
	  b9.translateBy(80, 20, 0);
	  
	sky = new FirePyramid(7, 7, 7, 7, 7);
	  sky.scaleBy(50,50,500);
	  sky.translateBy(50,50,-1);
	
	
  }
  

  // update view matrix based on eye, angles
  private void updateView(){
	
    camera.updateView();
  }

  private static double amount = 1;  // distance to move per step
  private static int mode = 2;
  private static int camAziAmount = 3;
  private static int camAltAmount = 3;
  

  protected void processInputs()
  {
    // process all waiting input events
    while( InputInfo.size() > 0 )
    {
      InputInfo info = InputInfo.get();

      if( info.kind == 'k' && (info.action == GLFW_PRESS || 
                               info.action == GLFW_REPEAT) )
      {
        int code = info.code, mods = info.mods;
        // System.out.println("code: " + code + " mods: " + mods );

         // keys to control the camera:

            if( code == GLFW_KEY_LEFT && mods == 0 )    
			{	camera.turn( camAziAmount );
				pyramid.rotateBy(camAziAmount, 0, 0, 1);
				degrees = degrees+3;
				radians = Math.toRadians(degrees);
			}
            else if( code == GLFW_KEY_RIGHT  && mods == 0 )
			{	camera.turn( -camAziAmount );
				pyramid.rotateBy(-camAziAmount, 0, 0, 1);
				degrees = degrees - 3;
				radians = Math.toRadians(degrees);
			}
            else if( code == GLFW_KEY_DOWN  && mods == 0 )  
			{	carSpeed = carSpeed - 0.1;
			}
            else if( code == GLFW_KEY_UP  && mods == 0 )
			{	carSpeed = carSpeed + 0.1;
			}
            

            else if( code == GLFW_KEY_L && mods == 0 )
              camera.turn( camAziAmount );
            else if( code == GLFW_KEY_R && mods == 0 )
              camera.turn( -camAziAmount );
            else if( code == GLFW_KEY_U && mods == 0 )
              camera.tilt( -camAltAmount );
            else if( code == GLFW_KEY_D && mods == 0 )
              camera.tilt( camAltAmount );

 
      }// input event is a key

      else if ( info.kind == 'm' )
      {// mouse moved
      }

      else if( info.kind == 'b' )
      {// button action

      }// 'b' action

    }// loop to process all input events

  }// processInputs

  protected void update()
  {
    if(count<400 && count > 0){
		car1.translateBy(0.2, 0, 0);
		car2.translateBy(0, 0.2, 0);
		car3.translateBy(0, -0.2, 0);
	}
	if(count==400){
		car1.rotateBy(180,0, 0, 1);
		car2.rotateBy(180,0, 0, 1);
		car3.rotateBy(180,0, 0, 1);
	}
	if(count>400 && count < 800){
		car1.translateBy(-0.2, 0, 0);
		car2.translateBy(0, -0.2, 0);
		car3.translateBy(0, 0.2, 0);
		
	}
	if(count==800){
		car1.rotateBy(180,0,0,1);
		car2.rotateBy(180,0, 0, 1);
		car3.rotateBy(180,0, 0, 1);
		count = -1;
	}
	
	if(count2<30 && count2 > 0){
		car4.translateBy(0.6, 0, 0);
		
	}
	if(count2==30){
		car4.rotateBy(90,0, 0, 1);
		
	}
	if(count2>30 && count2 < 60){
		car4.translateBy(0, 0.6, 0);
		
	}
	if(count2==60){
		car4.rotateBy(90,0,0,1);
	}
	if(count2<90 && count2 > 60){
		car4.translateBy(-0.6, 0, 0);
		
	}
	if(count2==90){
		car4.rotateBy(90,0, 0, 1);
		
	}
	if(count2>90 && count2 < 120){
		car4.translateBy(0, -0.6, 0);
		
	}
	if(count2==120){
		car4.rotateBy(90,0,0,1);
		count2 = -1;
	}
	
	count++;
	count2++;
	
	if(carSpeed != 0){
		pyramid.translateBy((Math.cos(radians)*carSpeed), (Math.sin(radians)*carSpeed), 0);
		camera.shift((Math.cos(radians)*carSpeed), (Math.sin(radians)*carSpeed), 0);
	}
	//if(carSpeed < 0){
	//	pyramid.translateBy((Math.cos(radians)*carSpeed), (Math.sin(radians)*carSpeed), 0);
	//	camera.shift((Math.cos(radians)*carSpeed), (Math.sin(radians)*carSpeed), 0);
	//}
	
	//  System.out.println( getStepNumber() + "================================" );

  }// update

  protected void display()
  {
    if( true ) {
      // clear console
      System.out.print("\033[H\033[2J");
      System.out.flush();

      camera.consoleDisplay();

    }// display info to console

    // start with empty soups for mutable soups
    if( mutableSoups != null )
      mutableSoups.cleanup();
    mutableSoups = new Soups( Pic.size() );

    // code to add all mutable triangles
    pyramid.draw( mutableSoups );
	b1.draw( mutableSoups );
	b2.draw( mutableSoups );
	b3.draw( mutableSoups );
	b4.draw( mutableSoups );
	b5.draw( mutableSoups );
	b6.draw( mutableSoups );
	b7.draw( mutableSoups );
	b8.draw( mutableSoups );
	b9.draw( mutableSoups );
	sky.draw(mutableSoups );
	car1.draw(mutableSoups);
	car2.draw(mutableSoups);
	car3.draw(mutableSoups);
	car4.draw(mutableSoups);
	
	//test.draw(mutableSoups);

    mutableSoups.sortByTexture();

    OpenGL.drawBackground();

    // draw the first-person view
    camera.activate();  // sets up viewport for first person
                        // and sends over proj and view matrices
                        // stored in camera
    frozenSoups.draw();
    mutableSoups.draw();

    // draw the map view
    mapViewer.activate();
    frozenSoups.draw();
    mutableSoups.draw();

  }

}// Ex10
