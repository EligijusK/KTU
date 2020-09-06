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
        val dontPlantMineUntil = bot.inputAsIntOrElse("dontPlantMineUntil", -1)
        val dontReleaseKamekadzeUntil = bot.inputAsIntOrElse("dontReleaseKamekadzeUntil", -1)
        val dontShootRocketUntil = bot.inputAsIntOrElse("dontShootRocketUntil", -1)
        // determine movement direction
        //directionValue(lastDirection) += 10 // try to break ties by favoring the last direction
        //val bestDirection45 = directionValue.zipWithIndex.maxBy(_._1)._2
        val direction = XY.fromDirection45(directionValue)
        
        bot.move(direction) //give straight direction
        bot.set("lastDirection" -> directionValue)
        
        /*if(dontShootRocketUntil < bot.time && bot.energy > 700){
            
            val unitDelta = XY.fromDirection45((lastDirection + 4) % 8)
            bot.spawn(unitDelta, "energy" -> 700, "mood" -> "Rocket")
            bot.set("dontShootRocketUntil" -> (bot.time + 30))
        
        
        }
        
        if(dontPlantMineUntil < bot.time && bot.energy > 500){
            
            val unitDelta = XY.fromDirection45((lastDirection + 4) % 8)
            bot.spawn(unitDelta, "energy" -> 500, "mood" -> "Mine")
            bot.set("dontPlantMineUntil" -> (bot.time + 20))
        
        } */
        
        if(dontReleaseKamekadzeUntil < bot.time && bot.energy > 100) { // fire defensive missile?
            bot.view.offsetToNearest('m') match {
              case Some(delta: XY) =>
                bot.set("rx" -> delta.x, "ry" -> delta.y)
                if (delta.length < 30) {
                    val unitDelta = XY.fromDirection45((lastDirection + 4) % 8)
                    bot.spawn(unitDelta, "energy" -> 500, "mood" -> "Kamikadze")
                    bot.set("dontReleaseKamekadzeUntil" -> (bot.time + 1))
                }
              case None =>
            }      
        }


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
            case "Mine" => reactAsMine(bot)
            case "Kamikadze" => reactAsKamekaze(bot)
            case "Rocket" => reactAsRocket(bot)
            // apsiraso atakos refrensas
            // apsiraso rinkimas
            case s: String => bot.log("unknown mood: " + s)
        }
    }
    
    def reactAsMine(bot: MiniBot) {
        bot.view.offsetToNearest('m') match {
          case Some(delta: XY) =>
            bot.set("rx" -> delta.x, "ry" -> delta.y)
            if (delta.length <= 3) {
              // yes -- blow it up!
              bot.explode(4)
            }
          case None =>
        }
        bot.view.offsetToNearest('s') match {
          case Some(delta: XY) =>
            bot.set("rx" -> delta.x, "ry" -> delta.y)
            if (delta.length <= 3) {
              // yes -- blow it up!
              bot.explode(4)
            }
          case None =>
        }
        bot.view.offsetToNearest('b') match {
          case Some(delta: XY) =>
            bot.set("rx" -> delta.x, "ry" -> delta.y)
            if (delta.length < 2) {
              // yes -- blow it up!
              bot.explode(4)
            }
          case None =>
        }
        
    }
    
    def reactAsKamekaze(bot: MiniBot) {
            val direction45 = analyzeViewAsBot(bot, 1000)
            val direction = XY.fromDirection45(direction45)
            bot.move(direction)
            bot.view.offsetToNearest('m') match {
                case Some(delta: XY) =>
                    bot.set("rx" -> delta.x, "ry" -> delta.y)
                    if (delta.length <= 4) {
                        // yes -- blow it up!
                        bot.explode(4)
                    }
                case None =>
            }
    }


    def reactAsRocket(bot: MiniBot) {
        bot.view.offsetToNearest('m') match {
          case Some(delta: XY) =>
            bot.set("rx" -> delta.x, "ry" -> delta.y)
            if (delta.length <= 3) {
              // yes -- blow it up!
              bot.explode(4)
            }
            else
            {
                val directionValue = delta.toDirection45
                val direction = XY.fromDirection45(directionValue)
                bot.move(direction) 
            }
          case None =>
        }
        bot.view.offsetToNearest('s') match {
          case Some(delta: XY) =>
            bot.set("rx" -> delta.x, "ry" -> delta.y)
            if (delta.length <= 3) {
              // yes -- blow it up!
              bot.explode(4)
            }
            else
            {
                val directionValue = delta.toDirection45
                val direction = XY.fromDirection45(directionValue)
                bot.move(direction) 
            }
          case None =>
        }
        bot.view.offsetToNearest('b') match {
          case Some(delta: XY) =>
            bot.set("rx" -> delta.x, "ry" -> delta.y)
            if (delta.length < 2) {
              // yes -- blow it up!
              bot.explode(4)
            }
            else
            {
                val directionValue = delta.toDirection45
                val direction = XY.fromDirection45(directionValue)
                bot.move(direction) 
            }
          case None =>
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
        val indexRel = view.indexFromRelPos(XY(0,0))

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
                        for (x <- -4 to 4) {
                          for (y <- -4 to 4) {
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
                    case 'P' =>
                        val pos = view.relPosFromIndex(i)
                        val stepDistance = cellRelPos.stepsTo(pos)
                        if (stepDistance == 1) cellWeights(i) += 100
                        else if (stepDistance == 2) cellWeights(i) += 300
                        else cellWeights(i) += 500
                        
                    case 'B' =>
                        val pos = view.relPosFromIndex(i)
                        val stepDistance = cellRelPos.stepsTo(pos)
                        if (stepDistance == 1) cellWeights(i) += 50
                        else if (stepDistance == 2) cellWeights(i) += 200
                        else cellWeights(i) += 520
                    
                    case 'b' =>
                        for (x <- -2 to 2) {
                            for (y <- -2 to 2) {
                                val pos = view.relPosFromIndex(i) + XY(x,y)
                                if(pos.isNonZero && !view.outOfBoundsRel(pos))
                                {
                                    val index = view.indexFromRelPos(pos)
                                    cellWeights(index) += 2500000
                                }
                            }
                        }

            
                    /*case 'p' => // bad plant: bad, but only if I step on it
                        cellWeights(i) += 100000*/
            
                    case 'W' => // wall: harmless, just don't walk into it
                        for (x <- -1 to 1) {
                          for (y <- -1 to 1) {
                            val pos = cellRelPos + XY(x, y)
                            if (pos.isNonZero && !view.outOfBoundsRel(pos)) {
                              cellWeights(view.indexFromRelPos(pos)) += 1500000
                            }
                          }
                        }
            
                    case '?' =>
                        cellWeights(i) += 1500000
                        
                    case _ => cellWeights(i) += 1
                    
                    }
                  }
                }

            }
            var direction45 = 0
            val lastDirection = bot.inputAsIntOrElse("lastDirection", 0)
            if (cells.contains('P') || cells.contains('B')) {
                //bot.log(cellWeights.deep.mkString(" "))
                //bot.log(cells.mkString(" "))
                var (closestFood, nearestEnemyMaster, nearestEnemySlave) = findClosestThings(cellCount, cells, view, cellWeights, bot)
                
                var (path, path_index, found) = aStarPathfind(cells, XY.Zero, view, closestFood, bot, cellWeights)
                
                if(found)
                {
                        //bot.log(cells)
                        //bot.log(path(path.size - 1).toString)
                        if(path(path.size - 1) != XY(0,0))
                        {

                            //index = view.indexFromAbsPos(path(path.size - 1))
                            direction45 = path(path.size - 1).toDirection45
                            bot.log(direction45.toString)
                            directionValue(direction45)
                        }
                    
                }
                else{
                    var index_tmp = view.indexFromRelPos(closestFood)
                    cellWeights(index_tmp) += cellWeights(index_tmp) * 2
                }

            }
            if(!cells.contains('P') && !cells.contains('B') || direction45 == lastDirection)
            {
                
                for(i <- 0 until cellCount) {
                    val cellRelPos = view.relPosFromIndex(i)
                    if(cellRelPos.isNonZero) {
                        val stepDistance = cellRelPos.stepCount
                        val value: Double = cells(i) match {
                            case 'm' => // another master: not dangerous, but an obstacle
                                nearestEnemyMaster = Some(cellRelPos)
                                if(stepDistance < 2) -1000 else 0
        
                            case 's' => // another slave: potentially dangerous?
                                nearestEnemySlave = Some(cellRelPos)
                                -100 / stepDistance
        
                            case 'S' => // out own slave
                                0.0
        
                            case 'B' => // good beast: valuable, but runs away
                                if(stepDistance == 1) 600
                                else if(stepDistance == 2) 300
                                else (150 - stepDistance * 15).max(10)
        
                            case 'P' => // good plant: less valuable, but does not run
                                if(stepDistance == 1) 500
                                else if(stepDistance == 2) 300
                                else (150 - stepDistance * 10).max(10)
        
                            case 'b' => // bad beast: dangerous, but only if very close
                                if(stepDistance < 4) -400 / stepDistance else -50 / stepDistance
        
                            case 'p' => // bad plant: bad, but only if I step on it
                                if(stepDistance < 2) -1000 else 0
        
                            case 'W' => // wall: harmless, just don't walk into it
                                if(stepDistance < 3) -1000 else 0
                            
                            case '?' =>
                                -100
                            case _ => 200.0
                        }
                        direction45 = cellRelPos.toDirection45
                        directionValue(direction45) += value
                        val bestDirection45 = directionValue.zipWithIndex.maxBy(_._1)._2
                        direction45 = bestDirection45
                    }
                }     
                
            }
            (direction45, nearestEnemyMaster, nearestEnemySlave)
        }
               
    

        def findClosestThings(cellCount: Integer, cells: String, view: View, weights: Array[Double], bot: Bot): (XY, XY, XY) = {
        var distances = Array.ofDim[Double](cellCount)
        var closestEnemyMaster: XY = XY(0,0)
        var closestEnemySlave: XY = XY(0,0)
        for (i <- 0 until cellCount) {
            if (cells(i) == 'P' || cells(i) == 'B') {
                var testWeight = 0.0
                val cellRelPos = view.relPosFromIndex(i)
                
                var temp_pos = cellRelPos + XY(-1, -1)
                var temp_index = 0
                temp_index = view.indexFromRelPos(temp_pos)
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                                    
                temp_pos = cellRelPos + XY(-1, 0)  
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                                    
                temp_pos = cellRelPos + XY(0, -1)
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, 0)
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, 0)
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, -1)
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, 1)
                if(!view.outOfBoundsRel(temp_pos))
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }

                testWeight = testWeight / 8
                bot.log(testWeight.toString)
                if (cellRelPos.isNonZero) {
                    val stepDistance = cellRelPos.stepCount
                    
                    distances(i) += stepDistance
                    if(testWeight > 1500000)
                    {
                        distances(i) += stepDistance / 2
                    }
                    if(testWeight < 1500000 && testWeight > 1000000 && bot.energy > 2500)
                    {
                        distances(i) -= stepDistance / 3
                    }
                    if(testWeight < 1000000)
                    {
                        distances(i) -= stepDistance / 2
                    }
                    //distances(i) += testWeight
                }
            } else if (cells(i) == 'm') {
                closestEnemyMaster = view.relPosFromIndex(i)
            } else if (cells(i) == 's') {
                closestEnemySlave = view.relPosFromIndex(i)
            }
        }
        
        

        (view.relPosFromIndex(distances.indexOf(distances.filter(_ > 0).min)),
            closestEnemyMaster,
            closestEnemySlave)
    }

    def findFarestThing(cellCount: Integer, cells: String, view: View, bot: Bot, weights: Array[Double]): (XY) = {
        var distances = Array.ofDim[Double](cellCount)
        for (i <- 0 until cellCount) {
            if (cells(i) == '_') {
                var testWeight = 0.0
                val cellRelPos = view.relPosFromIndex(i)
                
                var temp_pos = cellRelPos + XY(-1, -1)
                var temp_index = 0
                temp_index = view.indexFromRelPos(temp_pos)
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                                    
                temp_pos = cellRelPos + XY(-1, 0)  
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                                    
                temp_pos = cellRelPos + XY(0, -1)
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, 0)
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, 0)
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, -1)
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }
                
                temp_pos = cellRelPos + XY(1, 1)
                if(!view.outOfBoundsRel(temp_pos) && temp_pos.isNonZero)
                {
                temp_index = view.indexFromRelPos(temp_pos)
                testWeight += weights(temp_index)
                }

                testWeight = testWeight / 8
                bot.log(testWeight.toString)
                if (cellRelPos.isNonZero) {
                    val stepDistance = cellRelPos.stepCount
                    
                    distances(i) += stepDistance
                    if(testWeight > 1500000 && bot.energy > 2500)
                    {
                        distances(i) += stepDistance * 2
                    }
                    if(testWeight < 1500000 && testWeight > 1000000 && bot.energy > 2500)
                    {
                        distances(i) += stepDistance * 3
                    }
                    if(testWeight < 1000000)
                    {
                        distances(i) += stepDistance * 4
                    }
                    //distances(i) += testWeight
                }
            }
        }

        (view.relPosFromIndex(distances.indexOf(distances.filter(_ > 0).max)))
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
        var path_index = ListBuffer[Int]()

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
                            
                            if (!view.outOfBoundsRel(pos)) 
                            { 
                                if(pos == destination)
                                {
                                    parent_coordinates.update(index, pos)
                                    //parent()
                                    parent.update(index, parentIndex)
                                    var (temp_path, temp_path_index) = tracePath(parent, parent_coordinates, pos, startingPoint, view, bot)
                                    path = temp_path
                                    path_index = temp_path_index
                                    foundDest = true
                                    break
                                }
                                else if(closed_list(index) == false && isUnBlocked(cells, index))
                                {
                                    var average_weight = 0.0
                                    for(x_tmp <- -3 to 3)
                                    {
                                        for(y_tmp <- -3 to 3)
                                        {
                                            var temp_pos = loopingPos + XY(x_tmp, y_tmp)
                                            var temp_index = view.indexFromRelPos(pos)
                                            average_weight = average_weight + weights(temp_index)
                                            average_weight += weights(index)
                                        }
                                    }
                                    
                                    average_weight = average_weight / 32
                                    var gNew = 0.0
                                    if((x == 0 && y != 0) || (x != 0 && y == 0))
                                    {
                                       gNew = g(parentIndex) + 1.0; 
                                    }
                                    else
                                    {
                                        gNew = g(parentIndex) + 1.414;
                                    }
                                    gNew = gNew
                                    var hNew = calculateHValue(pos, destination)
                                    hNew = hNew
                                    var fNew = gNew + hNew + average_weight; 
    
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
        (path, path_index, foundDest)
        
    }
    
    def isUnBlocked(colums: String, index_check: Int): (Boolean) =
    { 
    // Returns true if the cell is not blocked else false 
        if (colums(index_check) != 'w' && colums(index_check) != '?' && colums(index_check) != 'b')
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
        var index = view.indexFromRelPos(last)
        var indexGo = ListBuffer[Int]()
        
        while(parent(index) != view.indexFromRelPos(dest))
        {

            Path = parent_coordinates(index) +=: Path
            indexGo = index +=: indexGo
            index = parent(index)
        }

        Path = parent_coordinates(index) +=: Path
        indexGo = index +=: indexGo
        (Path, indexGo)

    } 
    
     def analyzeViewAsBot(bot: Bot, masterVal: Int) = {
        var view = bot.view
        // cia suranda vieta kur eiti
        val directionValue = Array.ofDim[Double](8)
        var nearestEnemyMaster: Option[XY] = None
        var nearestEnemySlave: Option[XY] = None

        val cells = view.cells
        val cellCount = cells.length
        val cellWeights = Array.ofDim[Double](cellCount)
        val indexRel = view.indexFromRelPos(XY(0,0))

        //view.aStarPathfind(cellWeights, bot)
                //bot.log(cells.contains('P').toString)
                //bot.log(cells)
            if ( cells.contains('s') || cells.contains('b') || cells.contains('p') || cells.contains('w') || cells.contains('B') || cells.contains('P')) 
            {
                for(i <- 0 until cellCount) {
                  val cellRelPos = view.relPosFromIndex(i)
                  if(cellRelPos.isNonZero) {
                    cells(i) match {
                        
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
                    case 'P' =>
                        val pos = view.relPosFromIndex(i)
                        val stepDistance = cellRelPos.stepsTo(pos)
                        if (stepDistance == 1) cellWeights(i) += 100
                        else if (stepDistance == 2) cellWeights(i) += 300
                        else cellWeights(i) += 500
                        
                    case 'B' =>
                        val pos = view.relPosFromIndex(i)
                        val stepDistance = cellRelPos.stepsTo(pos)
                        if (stepDistance == 1) cellWeights(i) += 50
                        else if (stepDistance == 2) cellWeights(i) += 200
                        else cellWeights(i) += 520
                    
                    case 'b' =>
                        for (x <- -2 to 2) {
                            for (y <- -2 to 2) {
                                val pos = view.relPosFromIndex(i) + XY(x,y)
                                if(pos.isNonZero && !view.outOfBoundsRel(pos))
                                {
                                    val index = view.indexFromRelPos(pos)
                                    cellWeights(index) += 2500000
                                }
                            }
                        }

            
                    /*case 'p' => // bad plant: bad, but only if I step on it
                        cellWeights(i) += 100000*/
            
                    case 'W' => // wall: harmless, just don't walk into it
                        for (x <- -1 to 1) {
                          for (y <- -1 to 1) {
                            val pos = cellRelPos + XY(x, y)
                            if (pos.isNonZero && !view.outOfBoundsRel(pos)) {
                              cellWeights(view.indexFromRelPos(pos)) += 1500000
                            }
                          }
                        }
            
                    case '?' =>
                        cellWeights(i) += 1500000
                        
                    case _ => cellWeights(i) += 1
                    
                    }
                  }
                }

            }
            var direction45 = 0
            val lastDirection = bot.inputAsIntOrElse("lastDirection", 0)
            if (cells.contains('m')) {
                //bot.log(cellWeights.deep.mkString(" "))
                //bot.log(cells.mkString(" "))
                var (closestFood, nearestEnemyMaster, nearestEnemySlave) = findClosestThings(cellCount, cells, view, cellWeights, bot)
                
                if(nearestEnemyMaster.isNonZero)
                {
                    var (path, path_index, found) = aStarPathfind(cells, XY.Zero, view, nearestEnemyMaster, bot, cellWeights)
                    
                    if(found)
                    {
                            //bot.log(cells)
                            //bot.log(path(path.size - 1).toString)
                            if(path(path.size - 1) != XY(0,0))
                            {
    
                                //index = view.indexFromAbsPos(path(path.size - 1))
                                direction45 = path(path.size - 1).toDirection45
                                bot.log(direction45.toString)
                                directionValue(direction45)
                            }
                        
                    }
                    else{
                        var index_tmp = view.indexFromRelPos(closestFood)
                        cellWeights(index_tmp) += cellWeights(index_tmp) * 2
                    }
                }

            }
            else if(!cells.contains('m') || direction45 == lastDirection)
            {
                
                for(i <- 0 until cellCount) {
                    val cellRelPos = view.relPosFromIndex(i)
                    if(cellRelPos.isNonZero) {
                        val stepDistance = cellRelPos.stepCount
                        val value: Double = cells(i) match {
        
                            case 'm' =>
                                1000
        
                            case 'S' => // out own slave
                                0.0
        
                            case 'B' => // good beast: valuable, but runs away
                                if(stepDistance == 1) 600
                                else if(stepDistance == 2) 300
                                else (150 - stepDistance * 15).max(10)
        
                            case 'P' => // good plant: less valuable, but does not run
                                if(stepDistance == 1) 500
                                else if(stepDistance == 2) 300
                                else (150 - stepDistance * 10).max(10)
        
                            case 'b' => // bad beast: dangerous, but only if very close
                                if(stepDistance < 4) -400 / stepDistance else -50 / stepDistance
        
                            case 'p' => // bad plant: bad, but only if I step on it
                                if(stepDistance < 2) -1000 else 0
        
                            case 'W' => // wall: harmless, just don't walk into it
                                if(stepDistance < 3) -1000 else 0
                            
                            case '?' =>
                                -100
                            case _ => 200.0
                        }
                        direction45 = cellRelPos.toDirection45
                        directionValue(direction45) += value
                        val bestDirection45 = directionValue.zipWithIndex.maxBy(_._1)._2
                        direction45 = bestDirection45
                    }
                }     
                
            }
            (direction45)
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

