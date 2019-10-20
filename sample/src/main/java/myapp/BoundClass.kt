package myapp

import javax.inject.Inject

interface BoundClass

class BoundClassImpl @Inject constructor() : BoundClass