package com.puroguramingu.classification.bayes

/**
  * Created by mateusz on 2/12/16.
  */
object Dataset {

  def load = Seq[Posting](
    new Posting("my dog has flea problems help please".split(" ").toSeq, 0),
    new Posting("maybe not take him to dog park stupid".split(" ").toSeq, 1),
    new Posting("my dalmation is so cute I love him".split(" ").toSeq, 0),
    new Posting("stop posting stupid worthless garbage".split(" ").toSeq, 1),
    new Posting("mr licks ate my steak how to stop him".split(" ").toSeq, 0),
    new Posting("quit buying worthless dog food stupid".split(" ").toSeq, 1)
  )

  def vocabulary(dataset: Seq[Posting]) = dataset.flatMap(_.text).distinct.toList

  val defaultVocab = vocabulary(load)

  def words2Vec(vocabulary: List[String], seq: Seq[String]): Vector[Int] =
    vocabulary.map(word => seq.count(_.equals(word))).toVector

  def words2Vec(seq: Seq[String]): Vector[Int] = words2Vec(defaultVocab, seq)

  def loadVectorized = load.map(posting => new PostingVector(words2Vec(posting.text), posting.abusive))
}

case class Posting(text: Seq[String], abusive: Int)

case class PostingVector(words: Vector[Int], abusive: Int)

object NaiveBayes {

  def train(data: Seq[PostingVector]) = {
    if (data.isEmpty) throw new IllegalArgumentException("Training data cannot be empty.")

    val dataSize = data.size
    val words = data.head.words.size

    val pAbusive = data.map(_.abusive).sum / dataSize.toDouble

    val abusive = data.filter(_.abusive == 1).map(_.words)
    val p1Num = abusive.transpose.map(_.sum).map(_ + 1)
    /**
      * If I understand correctly we are starting our denominators at 2 here and not 1 because of Laplace smoothing.
      * Out alpha is 1 and our d is 2 since our observations are either 0 or 1 so 2 possibilities.
      * Correct me if I'm wrong.
      */
    val p1Denom = abusive.foldLeft(2.0){
      case (acc, vec) => acc + vec.sum
    }

    val nonAbusive = data.filter(_.abusive == 0).map(_.words)
    val p0Num = nonAbusive.transpose.map(_.sum).map(_ + 1)
    val p0Denom = nonAbusive.foldLeft(2.0){
      case (acc, vec) => acc + vec.sum
    }

    (p0Num.map(x => math.log(x / p0Denom)), p1Num.map(x => math.log(x / p1Denom)), pAbusive)
  }

  def classify(toClassify: Vector[Int], p0Vec: Seq[Double], p1Vec: Seq[Double], pClass1: Double): Int = {
    val p1 = toClassify.zip(p1Vec).map{ case (occ, prob) => occ * prob + math.log(pClass1) }.sum
    val p0 = toClassify.zip(p0Vec).map{ case (occ, prob) => occ * prob + math.log(1.0 - pClass1) }.sum
    if (p1 > p0) {
      return 1
    }
    0
  }
}