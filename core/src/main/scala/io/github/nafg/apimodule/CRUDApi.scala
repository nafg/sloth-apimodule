package io.github.nafg.apimodule

trait CRUDApi[F[_], L, O, K] {
  def create(orphan: O): F[K]

  def update(keyed: K): F[Unit]

  def delete(lookup: L): F[Unit]
}
