# peersim-pcbroadcast

_Keywords: causal broadcast, large and dynamic networks_

[PeerSim](http://peersim.sourceforge.net/) [1] implementation for preventive
causal broadcast. Instead of checking at each receipt if messages are ready to
be delivered, messages arrive ready by design. Instead of conveying control
information that increases linearly with the network size, messages convey
constant size control information. The delivery execution time goes from linear
to constant too.

Causal broadcast finally becomes an affordable and efficient middleware for
distributed protocols and applications in large and dynamic systems.


## References

[1] A. Montresor and M. Jelasity. Peersim: A scalable P2P
simulator. _Proceedings of the 9th International Conference on Peer-to-Peer
(P2P’09)_, Seattle, WA, Sep. 2009, pp. 99-100.


