// Example Bot #1: The Reference Bot


/** This bot builds a 'direction value map' that assigns an attractiveness score to
  * each of the eight available 45-degree directions. Additional behaviors:
  * - aggressive missiles: approach an enemy master, then explode
  * - defensive missiles: approach an enemy slave and annihilate it
  *
  * The master bot uses the following state parameters:
  *  - dontFireAggressiveMissileUntil
  *  - dontFireDefensiveMissileUntil
  *  - lastDirection
  * The mini-bots use the following state parameters:
  *  - mood = Aggressive | Defensive | Lurking
  *  - target = remaining offset to target location
  */
  
import scala.util.control.Breaks._
import scala.math.sqrt
import java.util
import scala.collection.mutable.ListBuffer
object ControlFunction
{
    def forMaster(bot: Bot) {
        val (directionValue, nearestEnemyMaster, nearestEnemySlave) = analyzeViewAsMaster(bot)

        val dontFireAggressiveMissileUntil = bot.inputAsIntOrElse("dontFireAggressiveMissileUntil", -1)
        val dontFireDefensiveMissileUntil = bot.inputAsIntOrElse("dontFireDefensiveMissileUntil", -1)
        val lastDirection = bot.inputAsIntOrElse("lastDirection", 0)

        // determine movement direction
        //directionValue(lastDirection) += 10 // try to break ties by favoring the last direction
        val bestDirection45 = directionValue.zipWithIndex.maxBy(_._1)._2
        val direction = XY.fromDirection45(bestDirection45)
        
        bot.move(direction) //give straight direction
        bot.set("lastDirection" -> bestDirection45)

        if(dontFireAggressiveMissileUntil < bot.time && bot.energy > 100) { // fire attack missile?
            nearestEnemyMaster match {
                case None =>            // no-on nearby
                case Some(relPos) =>    // a master is nearby
                    val unitDelta = relPos.signum
                    val remainder = relPos - unitDelta // we place slave nearer target, so subtract that from overall delta
                    bot.spawn(unitDelta, "mood" -> "Aggressive", "target" -> remainder)
                    bot.set("dontFireAggressiveMissileUntil" -> (bot.time + relPos.stepCount + 1))
            }
        }
        else
        if(dontFireDefensiveMissileUntil < bot.time && bot.energy > 100) { // fire defensive missile?
            nearestEnemySlave match {
                case None =>            // no-on nearby
                case Some(relPos) =>    // an enemy slave is nearby
                    if(relPos.stepCount < 8) {
                        // this one's getting too close!
                        val unitDelta = relPos.signum
                        val remainder = relPos - unitDelta // we place slave nearer target, so subtract that from overall delta
                        bot.spawn(unitDelta, "mood" -> "Defensive", "target" -> remainder)
                        bot.set("dontFireDefensiveMissileUntil" -> (bot.time + relPos.stepCount + 1))
                    }
            }
        // vieta kazkokiai atakai
        }
    }


    def forSlave(bot: MiniBot) {
        bot.inputOrElse("mood", "Lurking") match {
            case "Aggressive" => reactAsAggressiveMissile(bot)
            case "Defensive" => reactAsDefensiveMissile(bot)
            // apsiraso atakos refrensas
            // apsiraso rinkimas
            case s: String => bot.log("unknown mood: " + s)
        }
    }


    def reactAsAggressiveMissile(bot: MiniBot) {
        bot.view.offsetToNearest('m') match {
            case Some(delta: XY) =>
                // another master is visible at the given relative position (i.e. position delta)

                // close enough to blow it up?
                if(delta.length <= 2) {
                    // yes -- blow it up!
                    bot.explode(4)

                } else {
                    // no -- move closer!
                    bot.move(delta.signum)
                    bot.set("rx" -> delta.x, "ry" -> delta.y)
                }
            case None =>
                // no target visible -- follow our targeting strategy
                val target = bot.inputAsXYOrElse("target", XY.Zero)

                // did we arrive at the target?
                if(target.isNonZero) {
                    // no -- keep going
                    val unitDelta = target.signum // e.g. CellPos(-8,6) => CellPos(-1,1)
                    bot.move(unitDelta)

                    // compute the remaining delta and encode it into a new 'target' property
                    val remainder = target - unitDelta // e.g. = CellPos(-7,5)
                    bot.set("target" -> remainder)
                } else {
                    // yes -- but we did not detonate yet, and are not pursuing anything?!? => switch purpose
                    bot.set("mood" -> "Lurking", "target" -> "")
                    bot.say("Lurking")
                }
        }
    }


    def reactAsDefensiveMissile(bot: MiniBot) {
        bot.view.offsetToNearest('s') match {
            case Some(delta: XY) =>
                // another slave is visible at the given relative position (i.e. position delta)
                // move closer!
                bot.move(delta.signum)
                bot.set("rx" -> delta.x, "ry" -> delta.y)

            case None =>
                // no target visible -- follow our targeting strategy
                val target = bot.inputAsXYOrElse("target", XY.Zero)

                // did we arrive at the target?
                if(target.isNonZero) {
                    // no -- keep going
                    val unitDelta = target.signum // e.g. CellPos(-8,6) => CellPos(-1,1)
                    bot.move(unitDelta)

                    // compute the remaining delta and encode it into a new 'target' property
                    val remainder = target - unitDelta // e.g. = CellPos(-7,5)
                    bot.set("target" -> remainder)
                } else {
                    // yes -- but we did not annihilate yet, and are not pursuing anything?!? => switch purpose
                    bot.set("mood" -> "Lurking", "target" -> "")
                    bot.say("Lurking")
                }
        }
    }
    
    // funkcijos reactinimo vieta


    /** Analyze the view, building a map of attractiveness for the 45-degree directions and
      * recording other relevant data, such as the nearest elements of various kinds.
      */
    def analyzeViewAsMaster(bot: Bot) = {
        var view = bot.view
        // cia suranda vieta kur eiti
        val directionValue = Array.ofDim[Double](8)
        var nearestEnemyMaster: Option[XY] = None
        var nearestEnemySlave: Option[XY] = None

        val cells = view.cells
        val cellCount = cells.length
        val cellWeights = Array.ofDim[Double](cellCount)
        
        //view.aStarPathfind(cellWeights, bot)
                //bot.log(cells.contains('P').toString)
                //bot.log(cells)
            
            if (cells.contains('m') || cells.contains('s') || cells.contains('b') || cells.contains('p') || cells.contains('w')) 
            {
                for(i <- 0 until cellCount) {
                  val cellRelPos = view.relPosFromIndex(i)
                  if(cellRelPos.isNonZero) {
                    cells(i) match {
                      case 'm' => // another master: not dangerous, but an obstacle
                        nearestEnemyMaster = Some(cellRelPos)
                        for (x <- -1 to 1) {
                          for (y <- -1 to 1) {
                            val pos = cellRelPos + XY(x, y)
                            if (pos.isNonZero && !view.outOfBoundsRel(pos)) {
                              cellWeights(view.indexFromRelPos(pos)) += 10000
                            }
                          }
                        }
            
                      case 's' => // another slave: potentially dangerous?
                        nearestEnemySlave = Some(cellRelPos)
                        for (j <- 0 until cellCount) {
                          val pos = view.relPosFromIndex(j)
                          val stepDistance = cellRelPos.stepsTo(pos)
                          if (pos.isNonZero && stepDistance != 0) {
                            val stepDistance = cellRelPos.stepsTo(pos)
                            cellWeights(j) += 1000 / stepDistance
                          }
                        }
            
                      case 'b' => // bad beast: dangerous, but only if very close
                        for (j <- 0 until cellCount) {
                          val pos = view.relPosFromIndex(j)
                          val stepDistance = cellRelPos.stepsTo(pos)
                          if (pos.isNonZero && stepDistance != 0) {
                            if (stepDistance < 4) cellWeights(j) += 400 / stepDistance
                            else cellWeights(j) += 50 / stepDistance
                          }
                        }
            
                      case 'p' => // bad plant: bad, but only if I step on it
                        cellWeights(i) += 10000
            
                      case 'W' => // wall: harmless, just don't walk into it
                        cellWeights(i) += 10000
            
                      case _ => cellWeights(i) += 1
                    }
                  }
                }

            }
            
            if (cells.contains('P') || cells.contains('B')) {
                var (closestFood, secondClosestFood, innerMaster, innerSlave) = findClosestThings(cellCount, cells, view)
                
                var (path, found) = aStarPathfind(cells, XY.Zero, view, closestFood, bot, cellWeights)

                
                if(found)
                {

                        bot.log(path(path.size - 1).toString)
                        if(path(path.size - 1) != XY(0,0))
                        {
                            val direction45 = path(path.size - 1).toDirection45
                            directionValue(direction45) += 1000
                        }
                    
                }         
                  
            }
        
        (directionValue, nearestEnemyMaster, nearestEnemySlave)
    }

        def findClosestThings(cellCount: Integer, cells: String, view: View): (XY, XY, Option[XY], Option[XY]) = {
        var distances = Array.ofDim[Double](cellCount)
        var closestEnemyMaster: Option[XY] = None
        var closestEnemySlave: Option[XY] = None
        for (i <- 0 until cellCount) {
            if (cells(i) == 'P' || cells(i) == 'B') {
                val cellRelPos = view.relPosFromIndex(i)
                if (cellRelPos.isNonZero) {
                    val stepDistance = cellRelPos.stepCount
                    distances(i) += stepDistance
                }
            } else if (cells(i) == 'm') {
                closestEnemyMaster = Some(view.relPosFromIndex(i))
            } else if (cells(i) == 's') {
                closestEnemySlave = Some(view.relPosFromIndex(i))
            }
        }
        var minFirst = distances.indexOf(distances.filter(_ > 0).min)
        var minFilterValue = distances.filter(_ > 0).min
        distances = distances.filter(_ > minFilterValue)
        (view.relPosFromIndex(minFirst), view.relPosFromIndex(distances.indexOf(distances.min)),
            closestEnemyMaster,
            closestEnemySlave)
    }

    def aStarPathfind(cells: String, startingPoint: XY, view: View, destination: XY, bot: Bot, weights: Array[Double]) = {
        var open_list = ListBuffer[XY]()
        var open_list_f = ListBuffer[Double]()
        var closed_list = ListBuffer[Boolean]()
        var parent = ListBuffer[Int]()
        var parent_coordinates = ListBuffer[XY]()
        var g = ListBuffer[Float]()
        var f = ListBuffer[Float]()
        var h = ListBuffer[Float]()
        var foundDest = false
        var loopingPos = startingPoint;
        var path = ListBuffer[XY]()

        for(i <- 0 until cells.length)
        {
            closed_list = closed_list :+ false
            parent = parent :+ -1
            parent_coordinates = parent_coordinates :+ XY(-1,-1)
            g = g :+ Float.MaxValue
            f = f :+ Float.MaxValue
            h = h :+ Float.MaxValue
        }
        
        bot.log(cells.size.toString)
        
        var index = view.indexFromRelPos(startingPoint)
        f.update(index, (0.0).toFloat)
        g.update(index, (0.0).toFloat)
        h.update(index, (0.0).toFloat)
        parent.update(index, index)
        parent_coordinates.update(index, startingPoint)
        open_list = open_list :+ startingPoint
        open_list_f = open_list_f :+ 0.0
        
        var count = 0
        
        
        breakable{
            while(!open_list.isEmpty)
            {
                
                // prasideda while
                loopingPos = open_list(0)
                var parentIndex = view.indexFromRelPos(loopingPos)
                open_list.remove(0)
                open_list_f.remove(0)

                closed_list.update(index, false)

                for(x <- -1 to 1)
                {
                    for(y <- -1 to 1)
                    {
                        if((x != 0 && y != 0) ||(x == 0 && y != 0) || (x != 0 && y == 0))
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false)
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                     gNew = 10000 * gNew
                                    var hNew = calculateHValue(pos, destination)
                                    hNew = 10000 * hNew
                                    var fNew = gNew + hNew + weights(index); 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                    }
                    
                }
                
            }
            
        }
        (path, foundDest)
        
    }
    
    def aStarPathfindOptimized(cells: String, startingPoint: XY, view: View, destination: XY, bot: Bot) = {
        var open_list = ListBuffer[XY]()
        var open_list_f = ListBuffer[Double]()
        var closed_list = ListBuffer[Boolean]()
        var parent = ListBuffer[Int]()
        var parent_coordinates = ListBuffer[XY]()
        var g = ListBuffer[Float]()
        var f = ListBuffer[Float]()
        var h = ListBuffer[Float]()
        var foundDest = false
        var loopingPos = startingPoint;
        var path = ListBuffer[XY]()

        for(i <- 0 until cells.length)
        {
            closed_list = closed_list :+ false
            parent = parent :+ -1
            parent_coordinates = parent_coordinates :+ XY(-1,-1)
            g = g :+ Float.MaxValue
            f = f :+ Float.MaxValue
            h = h :+ Float.MaxValue
        }
        
        bot.log(cells.size.toString)
        
        var index = view.indexFromRelPos(startingPoint)
        f.update(index, (0.0).toFloat)
        g.update(index, (0.0).toFloat)
        h.update(index, (0.0).toFloat)
        parent.update(index, index)
        parent_coordinates.update(index, startingPoint)
        open_list = open_list :+ startingPoint
        open_list_f = open_list_f :+ 0.0
        
        var count = 0
        
        
        breakable{
            while(!open_list.isEmpty)
            {
                
                // prasideda while
                loopingPos = open_list(0)
                var parentIndex = view.indexFromRelPos(loopingPos)
                open_list.remove(0)
                open_list_f.remove(0)

                closed_list.update(index, false)

                var x = -1
                var y = -1
                        if(x == -1 && y == -1)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index) )
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                x = -1
                y = 0
                        if(x == -1 && y == 0)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                        
                x = 0
                y = -1
                        
                        if(x == 0 && y == -1)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                        
                x = 1
                y = -1
                        
                        if(x == 1 && y == -1)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                        
                x = -1
                y = 1
                        
                        if(x == -1 && y == 1)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                        
                x = 1
                y = 0
                        
                        if(x == -1 && y == 0)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                        
                x = 0
                y = 1
                        
                        if(x == 0 && y == -1)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                        
                x = 1
                y = 1
                        
                        if(x == 1 && y == 1)
                        {
                            
                            var pos = loopingPos + XY(x, y)
                            
                            index = view.indexFromRelPos(pos)
                            var absPos = view.absPosFromRelPos(pos)
                            var absIndex = view.indexFromAbsPos(absPos)
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    path = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var gNew = 0.0
                                    if(x == 0 || y == 0)
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                      
                                    var hNew = calculateHValue(pos, destination) 
                                    var fNew = gNew + hNew; 
    
                                    if (f(index) == Float.MaxValue || f(index) > fNew) 
                                    { 
    
                                        open_list = open_list :+ pos
                                        open_list_f = open_list_f :+ fNew
                                        //bot.log(index.toString)
                                        // Update the details of this cell
 
                                        f.update(index, fNew.toFloat)
                                        g.update(index, gNew.toFloat)
                                        h.update(index, hNew.toFloat)
                                        parent.update(index, parentIndex)
                                        parent_coordinates.update(index, loopingPos)
                                        
                                        // padaryti kelio classe kur bus indeksas ir posicija, ir poto ja updatinsim
        
                                    } 
        
                                }
                               
                                 
                            }
                        }
                 
                
            }
            
        }
        (path, foundDest)
        
    }
    
    def isUnBlocked(colums: String, index_check: Int): (Boolean) =
    { 
    // Returns true if the cell is not blocked else false 
        if (colums(index_check) != 'm' || colums(index_check) != 's' || colums(index_check) != 'b' || colums(index_check) != 'p' || colums(index_check) != 'w')
        {
            (true) 
        }
        else
        {
            (false) 
        }
    } 
    
    def calculateHValue(pos: XY, dest: XY): (Double)= 
    { 
    // Return using the distance formula 
    (sqrt((pos.x-dest.x)*(pos.x-dest.x) + (pos.y-dest.y)*(pos.y-dest.y))) 
    } 
    
    def tracePath(parent: ListBuffer[Int], parent_coordinates: ListBuffer[XY], last: XY, dest: XY, view: View, bot: Bot) = { 

      
        var Path = ListBuffer[XY]()
        var returnPath = ListBuffer[XY]()
        var index = view.indexFromRelPos(last)
        
        
        while(parent(index) != view.indexFromRelPos(dest))
        {
             Path = Path :+ parent_coordinates(index)
            index = parent(index)
        }
      
        Path = Path :+ parent_coordinates(index) 
         
        while (!Path.isEmpty)
        { 
            returnPath = returnPath :+ Path.last
            Path.remove(Path.length-1) 
            bot.log("kelias")
        }
        (returnPath)

    } 
    
    def removeCoord(coord: XY, list: List[XY]): (List[XY]) = {
        list diff List(coord)
        (list)
        
    }
    def removeFloat(num: Float, list: List[Float]): (List[Float]) = {
        list diff List(num)
        (list)
        
    }
    
    
}



// -------------------------------------------------------------------------------------------------
// Framework
// -------------------------------------------------------------------------------------------------

class ControlFunctionFactory {
    def create = (input: String) => {
        val (opcode, params) = CommandParser(input)
        opcode match {
            case "React" =>
                val bot = new BotImpl(params)
                if( bot.generation == 0 ) {
                    ControlFunction.forMaster(bot)
                } else {
                    ControlFunction.forSlave(bot)
                }
                bot.toString
            case _ => "" // OK
        }
    }
}


// -------------------------------------------------------------------------------------------------


trait Bot {
    // inputs
    def inputOrElse(key: String, fallback: String): String
    def inputAsIntOrElse(key: String, fallback: Int): Int
    def inputAsXYOrElse(keyPrefix: String, fallback: XY): XY
    def view: View
    def energy: Int
    def time: Int
    def generation: Int

    // outputs
    def move(delta: XY) : Bot
    def say(text: String) : Bot
    def status(text: String) : Bot
    def spawn(offset: XY, params: (String,Any)*) : Bot
    def set(params: (String,Any)*) : Bot
    def log(text: String) : Bot
}

trait MiniBot extends Bot {
    // inputs
    def offsetToMaster: XY

    // outputs
    def explode(blastRadius: Int) : Bot
}


case class BotImpl(inputParams: Map[String, String]) extends MiniBot {
    // input
    def inputOrElse(key: String, fallback: String) = inputParams.getOrElse(key, fallback)
    def inputAsIntOrElse(key: String, fallback: Int) = inputParams.get(key).map(_.toInt).getOrElse(fallback)
    def inputAsXYOrElse(key: String, fallback: XY) = inputParams.get(key).map(s => XY(s)).getOrElse(fallback)

    val view = View(inputParams("view"))
    val energy = inputParams("energy").toInt
    val time = inputParams("time").toInt
    val generation = inputParams("generation").toInt
    def offsetToMaster = inputAsXYOrElse("master", XY.Zero)


    // output

    private var stateParams = Map.empty[String,Any]     // holds "Set()" commands
    private var commands = ""                           // holds all other commands
    private var debugOutput = ""                        // holds all "Log()" output

    /** Appends a new command to the command string; returns 'this' for fluent API. */
    private def append(s: String) : Bot = { commands += (if(commands.isEmpty) s else "|" + s); this }

    /** Renders commands and stateParams into a control function return string. */
    override def toString = {
        var result = commands
        if(!stateParams.isEmpty) {
            if(!result.isEmpty) result += "|"
            result += stateParams.map(e => e._1 + "=" + e._2).mkString("Set(",",",")")
        }
        if(!debugOutput.isEmpty) {
            if(!result.isEmpty) result += "|"
            result += "Log(text=" + debugOutput + ")"
        }
        result
    }

    def log(text: String) = { debugOutput += text + "\n"; this }
    def move(direction: XY) = append("Move(direction=" + direction + ")")
    def say(text: String) = append("Say(text=" + text + ")")
    def status(text: String) = append("Status(text=" + text + ")")
    def explode(blastRadius: Int) = append("Explode(size=" + blastRadius + ")")
    def spawn(offset: XY, params: (String,Any)*) =
        append("Spawn(direction=" + offset +
            (if(params.isEmpty) "" else "," + params.map(e => e._1 + "=" + e._2).mkString(",")) +
            ")")
    def set(params: (String,Any)*) = { stateParams ++= params; this }
    def set(keyPrefix: String, xy: XY) = { stateParams ++= List(keyPrefix+"x" -> xy.x, keyPrefix+"y" -> xy.y); this }
}


// -------------------------------------------------------------------------------------------------


/** Utility methods for parsing strings containing a single command of the format
  * "Command(key=value,key=value,...)"
  */
object CommandParser {
    /** "Command(..)" => ("Command", Map( ("key" -> "value"), ("key" -> "value"), ..}) */
    def apply(command: String): (String, Map[String, String]) = {
        /** "key=value" => ("key","value") */
        def splitParameterIntoKeyValue(param: String): (String, String) = {
            val segments = param.split('=')
            (segments(0), if(segments.length>=2) segments(1) else "")
        }

        val segments = command.split('(')
        if( segments.length != 2 )
            throw new IllegalStateException("invalid command: " + command)
        val opcode = segments(0)
        val params = segments(1).dropRight(1).split(',')
        val keyValuePairs = params.map(splitParameterIntoKeyValue).toMap
        (opcode, keyValuePairs)
    }
}


// -------------------------------------------------------------------------------------------------


/** Utility class for managing 2D cell coordinates.
  * The coordinate (0,0) corresponds to the top-left corner of the arena on screen.
  * The direction (1,-1) points right and up.
  */
case class XY(x: Int, y: Int) {
    override def toString = x + ":" + y

    def isNonZero = x != 0 || y != 0
    def isZero = x == 0 && y == 0
    def isNonNegative = x >= 0 && y >= 0

    def updateX(newX: Int) = XY(newX, y)
    def updateY(newY: Int) = XY(x, newY)

    def addToX(dx: Int) = XY(x + dx, y)
    def addToY(dy: Int) = XY(x, y + dy)

    def +(pos: XY) = XY(x + pos.x, y + pos.y)
    def -(pos: XY) = XY(x - pos.x, y - pos.y)
    def *(factor: Double) = XY((x * factor).intValue, (y * factor).intValue)

    def distanceTo(pos: XY): Double = (this - pos).length // Phythagorean
    def length: Double = math.sqrt(x * x + y * y) // Phythagorean

    def stepsTo(pos: XY): Int = (this - pos).stepCount // steps to reach pos: max delta X or Y
    def stepCount: Int = x.abs.max(y.abs) // steps from (0,0) to get here: max X or Y

    def signum = XY(x.signum, y.signum)

    def negate = XY(-x, -y)
    def negateX = XY(-x, y)
    def negateY = XY(x, -y)

    /** Returns the direction index with 'Right' being index 0, then clockwise in 45 degree steps. */
    def toDirection45: Int = {
        val unit = signum
        unit.x match {
            case -1 =>
                unit.y match {
                    case -1 =>
                        if(x < y * 3) Direction45.Left
                        else if(y < x * 3) Direction45.Up
                        else Direction45.UpLeft
                    case 0 =>
                        Direction45.Left
                    case 1 =>
                        if(-x > y * 3) Direction45.Left
                        else if(y > -x * 3) Direction45.Down
                        else Direction45.LeftDown
                }
            case 0 =>
                unit.y match {
                    case 1 => Direction45.Down
                    case 0 => throw new IllegalArgumentException("cannot compute direction index for (0,0)")
                    case -1 => Direction45.Up
                }
            case 1 =>
                unit.y match {
                    case -1 =>
                        if(x > -y * 3) Direction45.Right
                        else if(-y > x * 3) Direction45.Up
                        else Direction45.RightUp
                    case 0 =>
                        Direction45.Right
                    case 1 =>
                        if(x > y * 3) Direction45.Right
                        else if(y > x * 3) Direction45.Down
                        else Direction45.DownRight
                }
        }
    }

    def rotateCounterClockwise45 = XY.fromDirection45((signum.toDirection45 + 1) % 8)
    def rotateCounterClockwise90 = XY.fromDirection45((signum.toDirection45 + 2) % 8)
    def rotateClockwise45 = XY.fromDirection45((signum.toDirection45 + 7) % 8)
    def rotateClockwise90 = XY.fromDirection45((signum.toDirection45 + 6) % 8)


    def wrap(boardSize: XY) = {
        val fixedX = if(x < 0) boardSize.x + x else if(x >= boardSize.x) x - boardSize.x else x
        val fixedY = if(y < 0) boardSize.y + y else if(y >= boardSize.y) y - boardSize.y else y
        if(fixedX != x || fixedY != y) XY(fixedX, fixedY) else this
    }
}


object XY {
    /** Parse an XY value from XY.toString format, e.g. "2:3". */
    def apply(s: String) : XY = { val a = s.split(':'); XY(a(0).toInt,a(1).toInt) }

    val Zero = XY(0, 0)
    val One = XY(1, 1)

    val Right     = XY( 1,  0)
    val RightUp   = XY( 1, -1)
    val Up        = XY( 0, -1)
    val UpLeft    = XY(-1, -1)
    val Left      = XY(-1,  0)
    val LeftDown  = XY(-1,  1)
    val Down      = XY( 0,  1)
    val DownRight = XY( 1,  1)

    def fromDirection45(index: Int): XY = index match {
        case Direction45.Right => Right
        case Direction45.RightUp => RightUp
        case Direction45.Up => Up
        case Direction45.UpLeft => UpLeft
        case Direction45.Left => Left
        case Direction45.LeftDown => LeftDown
        case Direction45.Down => Down
        case Direction45.DownRight => DownRight
    }

    def fromDirection90(index: Int): XY = index match {
        case Direction90.Right => Right
        case Direction90.Up => Up
        case Direction90.Left => Left
        case Direction90.Down => Down
    }

    def apply(array: Array[Int]): XY = XY(array(0), array(1))
}


object Direction45 {
    val Right = 0
    val RightUp = 1
    val Up = 2
    val UpLeft = 3
    val Left = 4
    val LeftDown = 5
    val Down = 6
    val DownRight = 7
}


object Direction90 {
    val Right = 0
    val Up = 1
    val Left = 2
    val Down = 3
}


// -------------------------------------------------------------------------------------------------




case class View(cells: String) {
    val size = math.sqrt(cells.length).toInt
    val center = XY(size / 2, size / 2)
    val cellCount = cells.length

    def apply(relPos: XY) = cellAtRelPos(relPos)

    def indexFromAbsPos(absPos: XY) = absPos.x + absPos.y * size
    def absPosFromIndex(index: Int) = XY(index % size, index / size)
    def absPosFromRelPos(relPos: XY) = relPos + center
    def cellAtAbsPos(absPos: XY) = cells.charAt(indexFromAbsPos(absPos))

    def indexFromRelPos(relPos: XY) = indexFromAbsPos(absPosFromRelPos(relPos))
    def relPosFromAbsPos(absPos: XY) = absPos - center
    def relPosFromIndex(index: Int) = relPosFromAbsPos(absPosFromIndex(index))
    def cellAtRelPos(relPos: XY) = cells.charAt(indexFromRelPos(relPos))

    def offsetToNearest(c: Char) = {
        val matchingXY = cells.view.zipWithIndex.filter(_._1 == c)
        if( matchingXY.isEmpty )
            None
        else {
            val nearest = matchingXY.map(p => relPosFromIndex(p._2)).minBy(_.length)
            Some(nearest)
        }
    }
    
    def outOfBoundsRel(relPos: XY) = {
        if(math.abs(relPos.x) > center.x || math.abs(relPos.y) > center.y){
            true
        }
        else{
            false
        }
    }
    
    def outOfBoundsAbs(absPos: XY) = {
        if(absPos.x < 0 || absPos.x > (size-1) || absPos.y < 0 || absPos.y > (size-1)){
            true
        }
        else{
            false
        }
    }


        
    
    
    
    
}

