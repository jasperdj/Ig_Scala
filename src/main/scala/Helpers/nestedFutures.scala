package Helpers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
  * Created by a623557 on 2-5-2016.
  */



trait ApplicativeFunctor[A, F[A]] {
  def apply[X](a:X): F[X]
  def map[B](f: A => B): F[B]
}

trait Monad[A, M[A]] extends ApplicativeFunctor[A, M] {
  def flatMMap[B](f: A => M[B]): M[B]
}


class OptionT[A, M[A]] (m:ApplicativeFunctor[Option[A], M]) extends ApplicativeFunctor[A, ({type T[X]=M[Option[X]]})#T] {
  override def apply[X](a:X): M[Option[X]] = m(Option(a))
  override def map[B](f: A => B): M[Option[B]] =
    m map {(x: Option[A]) => x map f}
}

class OptionTM[A, M[A]](m:Monad[Option[A], M]) extends OptionT[A, M](m) with Monad[A, ({type T[X]=M[Option[X]]})#T] {
  override def flatMMap [B] (f:A => M[Option[B]]) : M[Option[B]] =
    m flatMMap {
      case Some(z) => f(z)
      case None => m(None)
    }
}
