
import random
import math

def generatemap(numASV, numOb):

    f = open('4ASV-5OBS2.txt','w')
    f.write(str(numASV))
    
    Start_posx = float(random.randint(5,10))/100;
    Start_posy = float(random.randint(30,70))/100;
    ASVStringstart = str(Start_posx) + " " + str(Start_posy);

    th = 0;

    # Set up ASVs start pos
    for x in range (0, numASV-1):
        new_posx = Start_posx + 0.05;
        new_posy = Start_posy;
        th = float(random.randint(0,1) + th);
        new_posx = Start_posx + 0.05*math.sin(th) - 0*math.cos(th);
        new_posy = Start_posy + 0.05*math.cos(th) + 0*math.sin(th);
        ASVStringstart = ASVStringstart + " " + str(new_posx) + " " + str(new_posy);
        Start_posx = new_posx;
        Start_posy = new_posy;
        
            
    f.write(" \n")
    f.write(ASVStringstart)


    # Set up ASVs end pos
    End_posx = float(random.randint(70,80))/100;
    End_posy = float(random.randint(30,70))/100;
    ASVStringend = str(End_posx) + " " + str(End_posy);

    th = 0;
    for x in range (0, numASV-1):
        new_posx = End_posx + 0.05;
        new_posy = End_posy;
        th = float(random.randint(0,1) + th);
        new_posx = End_posx + 0.05*math.sin(th) - 0*math.cos(th);
        new_posy = End_posy + 0.05*math.cos(th) + 0*math.sin(th);
        ASVStringend = ASVStringend + " " + str(new_posx) + " " + str(new_posy);
        End_posx = new_posx;
        End_posy = new_posy;

    f.write(" \n")
    f.write(ASVStringend)


    f.write(" \n")
    f.write(str(numOb))
    last = 0;

    

    startpos = 30
    endpos = 70
    lines = numOb/2 + numOb/2-1;
    Difference =  endpos - startpos
    widthlength = Difference/lines
    
    # Set up ASVs obs
    for x in range (numOb/2):
        thickness = float(random.randint(10,10+40/numOb))/100
        height = float(random.randint(10,60))/100
        xvalue = (startpos+(x*2)*widthlength + float(random.randint(-widthlength,widthlength)))/100
        width = float(random.randint(1,widthlength))/100;
        minheight = float(random.randint(0,30))/100;
        maxheight = float(random.randint(70,100+minheight*100))/100;
        last = xvalue + thickness
        
        
        TopLeftx = xvalue;
        TopLefty = maxheight;
        
        TopRightx = xvalue + width;
        TopRighty = maxheight;
        
        BottomLeftx = xvalue;
        BottomLefty = height + thickness;
        
        BottomRightx = xvalue + width;
        BottomRighty = height + thickness;
        
        ObsStringend = str(BottomLeftx) + " " + str(BottomLefty) + " " + str(BottomRightx) + " " + str(BottomRighty) + " " + str(TopRightx) + " " + str(TopRighty) + " " + str(TopLeftx) + " " + str(TopLefty);


        xvalue = (startpos+(x*2)*widthlength + float(random.randint(-widthlength,widthlength)))/100
        
        f.write(" \n")
        f.write(str(ObsStringend))

        TopLeftx = xvalue;
        TopLefty = height;
        
        TopRightx = xvalue + width;
        TopRighty = height;
        
        BottomLeftx = xvalue;
        BottomLefty = minheight;
        
        BottomRightx = xvalue + width;
        BottomRighty = minheight;
        
        ObsStringend = str(BottomLeftx) + " " + str(BottomLefty) + " " + str(BottomRightx) + " " + str(BottomRighty) + " " + str(TopRightx) + " " + str(TopRighty) + " " + str(TopLeftx) + " " + str(TopLefty);

        f.write(" \n")
        f.write(str(ObsStringend))  




        thickness = float(random.randint(10,10+40/numOb))/100
        height = float(random.randint(10,60))/100
        xvalue = (startpos+(x*2)*widthlength + float(random.randint(-widthlength,widthlength)))/100
        width = float(random.randint(1,widthlength))/100;
        minheight = float(random.randint(startpos,startpos+10))/100;
        maxheight = float(random.randint(endpos-20,endpos))/100;
        last = xvalue + thickness
        
        
        TopLeftx = maxheight;
        TopLefty = xvalue;
        
        TopRightx = maxheight;
        TopRighty = xvalue + width;
        
        BottomLeftx = height + thickness;
        BottomLefty = xvalue;
        
        BottomRightx = height + thickness;
        BottomRighty = xvalue + width;
        
        ObsStringend = str(BottomLeftx) + " " + str(BottomLefty) + " " + str(BottomRightx) + " " + str(BottomRighty) + " " + str(TopRightx) + " " + str(TopRighty) + " " + str(TopLeftx) + " " + str(TopLefty);


        xvalue = (startpos+(x*2)*widthlength + float(random.randint(-widthlength,widthlength)))/100
        
        f.write(" \n")
        f.write(str(ObsStringend))

        TopLeftx = height;
        TopLefty = xvalue;
        
        TopRightx = height;
        TopRighty = xvalue + width;
        
        BottomLeftx = minheight;
        BottomLefty = xvalue;
        
        BottomRightx = minheight;
        BottomRighty = xvalue + width;
        
        ObsStringend = str(BottomLeftx) + " " + str(BottomLefty) + " " + str(BottomRightx) + " " + str(BottomRighty) + " " + str(TopRightx) + " " + str(TopRighty) + " " + str(TopLeftx) + " " + str(TopLefty);

        f.write(" \n")
        f.write(str(ObsStringend))  
    f.close()


# Create map (ASVs first, num obs second (makes obs in groups of 4)
generatemap(4, 5)
